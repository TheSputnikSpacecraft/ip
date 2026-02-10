import heapq
import time
from dataclasses import dataclass
from enum import IntEnum


# Imports for isinstance checks (assuming environment is available)
from grid_adventure.grid import GridState
from grid_adventure.env import ImageObservation
from grid_adventure.step import Action
from grid_adventure.entities import (
    AgentEntity, WallEntity, BoxEntity, CoinEntity, GemEntity,
    KeyEntity, LockedDoorEntity, UnlockedDoorEntity,
    SpeedPowerUpEntity, ShieldPowerUpEntity, PhasingPowerUpEntity,
    ExitEntity, FloorEntity, LavaEntity
)

@dataclass(frozen=True, slots=True)
class SearchState:
    agent_pos: tuple[int, int]

    gems_mask: int
    keys_mask: int
    doors_open_mask: int

    boots_mask: int
    ghosts_mask: int
    shields_mask: int

    keys_held: int
    box_positions: tuple[tuple[int, int], ...]

    speed_turns: int
    phasing_turns: int
    shield_uses: int
    hp: int


class Node:
    __slots__ = ("state", "parent", "action", "g", "h", "f")
    def __init__(self, state: SearchState, parent=None, action=None, g=0, h=0):
        self.state = state
        self.parent = parent
        self.action = action
        self.g = g
        self.h = h
        self.f = g + h


class Agent:
    def __init__(self):
        self.plan: list[Action] = []
        self.static_info_parsed = False

        # "Ghost memory" to avoid 1-turn parsing glitches causing executor to think ghost=off.
        # This stores "phasing turns remaining BEFORE this turn is spent".
        self._phasing_shadow_turns = 0

        # Static info
        self.width = 0
        self.height = 0
        self.walls: set[tuple[int, int]] = set()
        self.lava_positions: set[tuple[int, int]] = set()
        self.exit_pos: tuple[int, int] | None = None

        self.initial_gems: list[tuple[int, int]] = []
        self.initial_keys: list[tuple[int, int]] = []
        self.initial_doors: list[tuple[int, int]] = []
        self.initial_boots: list[tuple[int, int]] = []
        self.initial_ghosts: list[tuple[int, int]] = []
        self.initial_shields: list[tuple[int, int]] = []

        self.gem_idx: dict[tuple[int, int], int] = {}
        self.key_idx: dict[tuple[int, int], int] = {}
        self.door_idx: dict[tuple[int, int], int] = {}
        self.boots_idx: dict[tuple[int, int], int] = {}
        self.ghosts_idx: dict[tuple[int, int], int] = {}
        self.shields_idx: dict[tuple[int, int], int] = {}

        # Reachability without phasing (computed once during parse_static_info)
        self.reachable_no_phase: set[tuple[int, int]] = set()

        # Budgets (tuneable)
        self.time_budget_main = 0.22
        self.time_budget_eval = 0.06
        self.max_expansions_main = 70000
        self.max_expansions_eval = 18000

        # Coin cluster harvest (bounded)
        self.coin_candidates_k = 14
        self.coin_candidate_radius = 10
        self.coin_harvest_horizon = 14
        self.coin_harvest_expansions = 25000

        self.last_expansions = 0

    # ---------------- Core API ----------------
    def step(self, obs) -> Action:
        gs = self._ensure_gridstate(obs)
        if gs is None:
            return Action.WAIT

        if not self.static_info_parsed:
            self.parse_static_info(gs)
            self.static_info_parsed = True

        if getattr(gs, "win", False) or getattr(gs, "lose", False):
            return Action.WAIT

        agent_pos = self.find_agent_pos(gs)
        if agent_pos is None:
            return Action.WAIT

        agent_obj = self._find_agent_obj(gs, agent_pos)
        hp, keys_held, speed_turns, phasing_turns, shield_uses = (5, 0, 0, 0, 0)
        if agent_obj is not None:
            hp, keys_held, speed_turns, phasing_turns, shield_uses = self._read_agent_status(agent_obj)

        # ---- Shadow phasing sync (pre-turn) ----
        # If env reports phasing, trust it and shadow it.
        if phasing_turns > 0:
            self._phasing_shadow_turns = phasing_turns
        # If env reports 0 but we still think we have shadow turns, use shadow for logic.
        if phasing_turns == 0 and self._phasing_shadow_turns > 0:
            phasing_turns = self._phasing_shadow_turns

        # Mandatory: pick gem immediately.
        if self._cell_has(gs, agent_pos, GemEntity, "gem"):
            self.plan = []
            act = Action.PICK_UP
            # Spend the turn (phasing, if active) after selecting action.
            if self._phasing_shadow_turns > 0:
                self._phasing_shadow_turns = max(0, self._phasing_shadow_turns - 1)
            return act

        # ALWAYS pick coin if standing on it (net +2 reward: +5 - 3).
        if self._cell_has(gs, agent_pos, CoinEntity, "coin"):
            self.plan = []
            act = Action.PICK_UP
            if self._phasing_shadow_turns > 0:
                self._phasing_shadow_turns = max(0, self._phasing_shadow_turns - 1)
            return act

        # If plan says PICK_UP, just do it (prevents dithering).
        if self.plan and self.plan[0] == Action.PICK_UP:
            act = self.plan.pop(0)
            if self._phasing_shadow_turns > 0:
                self._phasing_shadow_turns = max(0, self._phasing_shadow_turns - 1)
            return act

        # ---------- Ghost mode: strictly objective, no detours ----------
        if phasing_turns > 0:
            if not self.plan:
                self.plan = self._plan_to_win(gs, objective_only=True)

            if self.plan:
                nxt = self.plan.pop(0)
                # During phasing, do NOT let door-handling / feasibility logic cancel wall-crossing moves.
                act = nxt
                if self._phasing_shadow_turns > 0:
                    self._phasing_shadow_turns = max(0, self._phasing_shadow_turns - 1)
                return act

            greedy = self._greedy_objective_move(gs, agent_pos, speed_turns, phasing_turns)
            act = greedy if greedy is not None else Action.WAIT
            if self._phasing_shadow_turns > 0:
                self._phasing_shadow_turns = max(0, self._phasing_shadow_turns - 1)
            return act

        # ---------- If locked doors exist, ensure we behave sensibly ----------
        if not self.plan:
            self.plan = self._choose_best_plan(gs, keys_held)

        if not self.plan:
            fb = self._fallback_move(gs, agent_pos, hp, shield_uses)
            act = fb if fb is not None else Action.WAIT
            return act

        nxt = self.plan.pop(0)
        act = self._execute_with_door_handling(gs, agent_pos, nxt, keys_held)
        return act

    def info(self):
        return {"last_expansions": self.last_expansions, "plan_len": len(self.plan)}

    # ---------------- Execution helpers ----------------
    def _execute_with_door_handling(self, gs: GridState, agent_pos, nxt: Action, keys_held: int) -> Action:
        # If movement would step into locked door, react correctly.
        if nxt in (Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT):
            npos = self._next_pos_for_move(agent_pos, nxt)
            if self._is_locked_door_cell(gs, npos):
                if keys_held > 0:
                    self.plan = []
                    return Action.USE_KEY
                self.plan = self._plan_key_then_win(gs)
                if self.plan:
                    return self.plan.pop(0)
                return Action.WAIT

        if nxt == Action.USE_KEY:
            return nxt

        if nxt in (Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT):
            if not self._action_feasible_now(gs, nxt):
                self.plan = []
                repl = self._choose_best_plan(gs, keys_held=keys_held)
                self.plan = repl
                if self.plan:
                    nn = self.plan.pop(0)
                    return nn if self._action_feasible_now(gs, nn) else Action.WAIT
                return Action.WAIT

        return nxt

    # ---------------- Observation parsing ----------------
    def _ensure_gridstate(self, obs):
        if isinstance(obs, GridState):
            return obs
        if hasattr(obs, "grid_state") and isinstance(obs.grid_state, GridState):
            return obs.grid_state
        return None

    def find_agent_pos(self, state: GridState):
        for x in range(state.width):
            for y in range(state.height):
                for o in state.objects_at((x, y)):
                    if isinstance(o, AgentEntity) or getattr(o.appearance, "name", "") == "human":
                        return (x, y)
        return None

    def _find_agent_obj(self, state: GridState, pos):
        for o in state.objects_at(pos):
            if isinstance(o, AgentEntity) or getattr(o.appearance, "name", "") == "human":
                return o
        return None

    def _cell_has(self, state: GridState, pos, cls, name: str) -> bool:
        for o in state.objects_at(pos):
            nm = getattr(o.appearance, "name", "").lower()
            if isinstance(o, cls) or nm == name:
                return True
        return False

    def _is_locked_door_cell(self, gs: GridState, pos) -> bool:
        for o in gs.objects_at(pos):
            nm = getattr(o.appearance, "name", "").lower()
            if isinstance(o, LockedDoorEntity) or nm == "locked":
                return True
        return False

    def _count_floor_keys(self, gs: GridState) -> int:
        cnt = 0
        for x in range(self.width):
            for y in range(self.height):
                for o in gs.objects_at((x, y)):
                    nm = getattr(o.appearance, "name", "").lower()
                    if isinstance(o, KeyEntity) or nm == "key":
                        cnt += 1
                        break
        return cnt

    def _doors_required_in_plan(self, plan: list[Action]) -> int:
        return sum(1 for a in plan if a == Action.USE_KEY)

    def _next_pos_for_move(self, pos, action):
        moves = {Action.UP:(0,-1), Action.DOWN:(0,1), Action.LEFT:(-1,0), Action.RIGHT:(1,0)}
        dx, dy = moves[action]
        return (pos[0] + dx, pos[1] + dy)

    def parse_static_info(self, grid_state: GridState):
        self.width = grid_state.width
        self.height = grid_state.height

        self.walls.clear()
        self.lava_positions.clear()
        self.exit_pos = None

        self.initial_gems.clear()
        self.initial_keys.clear()
        self.initial_doors.clear()
        self.initial_boots.clear()
        self.initial_ghosts.clear()
        self.initial_shields.clear()

        for x in range(self.width):
            for y in range(self.height):
                pos = (x, y)
                for obj in grid_state.objects_at(pos):
                    nm = getattr(obj.appearance, "name", "").lower()
                    if isinstance(obj, WallEntity) or nm == "wall":
                        self.walls.add(pos)
                    elif isinstance(obj, LavaEntity) or nm == "lava":
                        self.lava_positions.add(pos)
                    elif isinstance(obj, ExitEntity) or nm == "exit":
                        self.exit_pos = pos
                    elif isinstance(obj, GemEntity) or nm == "gem":
                        self.initial_gems.append(pos)
                    elif isinstance(obj, KeyEntity) or nm == "key":
                        self.initial_keys.append(pos)
                    elif isinstance(obj, LockedDoorEntity) or nm == "locked":
                        self.initial_doors.append(pos)
                    elif isinstance(obj, SpeedPowerUpEntity) or nm == "boots":
                        self.initial_boots.append(pos)
                    elif isinstance(obj, PhasingPowerUpEntity) or nm == "ghost":
                        self.initial_ghosts.append(pos)
                    elif isinstance(obj, ShieldPowerUpEntity) or nm == "shield":
                        self.initial_shields.append(pos)

        self.initial_gems.sort()
        self.initial_keys.sort()
        self.initial_doors.sort()
        self.initial_boots.sort()
        self.initial_ghosts.sort()
        self.initial_shields.sort()

        self.gem_idx = {p: i for i, p in enumerate(self.initial_gems)}
        self.key_idx = {p: i for i, p in enumerate(self.initial_keys)}
        self.door_idx = {p: i for i, p in enumerate(self.initial_doors)}
        self.boots_idx = {p: i for i, p in enumerate(self.initial_boots)}
        self.ghosts_idx = {p: i for i, p in enumerate(self.initial_ghosts)}
        self.shields_idx = {p: i for i, p in enumerate(self.initial_shields)}
        
        # Compute reachability without phasing (from agent's starting position)
        self._compute_reachable_no_phase(grid_state)
    
    def _compute_reachable_no_phase(self, grid_state: GridState):
        """
        BFS from agent's starting position with normal movement rules
        (walls block, locked doors block, boxes block).
        Stores reachable cells in self.reachable_no_phase.
        """
        agent_start = self.find_agent_pos(grid_state)
        if agent_start is None:
            return
        
        self.reachable_no_phase.clear()
        visited = {agent_start}
        queue = [agent_start]
        head = 0
        
        # Get initial box positions
        box_positions = set()
        for x in range(self.width):
            for y in range(self.height):
                for obj in grid_state.objects_at((x, y)):
                    if isinstance(obj, BoxEntity) or getattr(obj.appearance, "name", "").lower() == "box":
                        box_positions.add((x, y))
                        break
        
        while head < len(queue):
            cx, cy = queue[head]
            head += 1
            self.reachable_no_phase.add((cx, cy))
            
            for dx, dy in [(0, 1), (0, -1), (1, 0), (-1, 0)]:
                nx, ny = cx + dx, cy + dy
                npos = (nx, ny)
                
                if not (0 <= nx < self.width and 0 <= ny < self.height):
                    continue
                if npos in visited:
                    continue
                
                # Check if blocked (walls, locked doors, boxes)
                if npos in self.walls:
                    continue
                if npos in self.door_idx:  # All doors are locked initially
                    continue
                if npos in box_positions:
                    continue
                
                visited.add(npos)
                queue.append(npos)

    # ---------------- Status parsing ----------------
    def _read_agent_status(self, agent_obj):
        hp = getattr(getattr(agent_obj, "health", None), "current_health", 5)

        keys_held = 0
        if hasattr(agent_obj, "inventory_list"):
            for item in agent_obj.inventory_list:
                nm = getattr(item.appearance, "name", "").lower()
                if isinstance(item, KeyEntity) or nm == "key":
                    keys_held += 1

        speed_turns = 0
        phasing_turns = 0
        shield_uses = 0

        if hasattr(agent_obj, "status_list"):
            for stat in agent_obj.status_list:
                nm = getattr(stat.appearance, "name", "").lower()
                remaining = 0

                if hasattr(stat, "component_list"):
                    for comp in stat.component_list:
                        cn = comp.__class__.__name__
                        if cn == "TimeLimit" and hasattr(comp, "time_remaining"):
                            remaining = max(remaining, int(comp.time_remaining))
                        elif cn == "UsageLimit" and hasattr(comp, "uses_remaining"):
                            remaining = max(remaining, int(comp.uses_remaining))

                if remaining == 0:
                    for attr in ("time_remaining", "uses_remaining", "duration", "uses"):
                        if hasattr(stat, attr):
                            try:
                                remaining = max(remaining, int(getattr(stat, attr)))
                            except Exception:
                                pass

                if nm == "boots":
                    speed_turns = max(speed_turns, remaining)
                elif nm == "ghost":
                    phasing_turns = max(phasing_turns, remaining)
                elif nm == "shield":
                    shield_uses = max(shield_uses, remaining)

        return int(hp), int(keys_held), int(speed_turns), int(phasing_turns), int(shield_uses)

    # ---------------- Search-state extraction ----------------
    def get_search_state(self, grid_state: GridState) -> SearchState | None:
        agent_pos = None
        hp = 5
        keys_held = 0
        speed_turns = 0
        phasing_turns = 0
        shield_uses = 0

        box_positions: list[tuple[int, int]] = []

        gems_present = set()
        keys_present = set()
        boots_present = set()
        ghosts_present = set()
        shields_present = set()
        doors_locked = set()

        for x in range(self.width):
            for y in range(self.height):
                pos = (x, y)
                for obj in grid_state.objects_at(pos):
                    nm = getattr(obj.appearance, "name", "").lower()
                    if isinstance(obj, AgentEntity) or nm == "human":
                        agent_pos = pos
                        hp, keys_held, speed_turns, phasing_turns, shield_uses = self._read_agent_status(obj)
                        # Apply shadow if parser glitches.
                        if phasing_turns == 0 and self._phasing_shadow_turns > 0:
                            phasing_turns = self._phasing_shadow_turns
                    elif isinstance(obj, BoxEntity) or nm == "box":
                        box_positions.append(pos)
                    elif isinstance(obj, GemEntity) or nm == "gem":
                        gems_present.add(pos)
                    elif isinstance(obj, KeyEntity) or nm == "key":
                        keys_present.add(pos)
                    elif isinstance(obj, SpeedPowerUpEntity) or nm == "boots":
                        boots_present.add(pos)
                    elif isinstance(obj, PhasingPowerUpEntity) or nm == "ghost":
                        ghosts_present.add(pos)
                    elif isinstance(obj, ShieldPowerUpEntity) or nm == "shield":
                        shields_present.add(pos)
                    elif isinstance(obj, LockedDoorEntity) or nm == "locked":
                        doors_locked.add(pos)

        if agent_pos is None:
            return None

        def mask_from_present(initial_list, present_set):
            m = 0
            for i, p in enumerate(initial_list):
                if p not in present_set:
                    m |= (1 << i)
            return m

        gems_mask = mask_from_present(self.initial_gems, gems_present)
        keys_mask = mask_from_present(self.initial_keys, keys_present)
        boots_mask = mask_from_present(self.initial_boots, boots_present)
        ghosts_mask = mask_from_present(self.initial_ghosts, ghosts_present)
        shields_mask = mask_from_present(self.initial_shields, shields_present)

        doors_open_mask = 0
        for i, p in enumerate(self.initial_doors):
            if p not in doors_locked:
                doors_open_mask |= (1 << i)

        box_positions.sort()

        return SearchState(
            agent_pos=agent_pos,
            gems_mask=gems_mask,
            keys_mask=keys_mask,
            doors_open_mask=doors_open_mask,
            boots_mask=boots_mask,
            ghosts_mask=ghosts_mask,
            shields_mask=shields_mask,
            keys_held=keys_held,
            box_positions=tuple(box_positions),
            speed_turns=speed_turns,
            phasing_turns=phasing_turns,
            shield_uses=shield_uses,
            hp=hp,
        )

    # ---------------- Goal + heuristics ----------------
    def _is_goal(self, st: SearchState) -> bool:
        all_gems = (1 << len(self.initial_gems)) - 1
        return (st.gems_mask == all_gems) and (st.agent_pos == self.exit_pos)

    def _nearest_required_target(self, st: SearchState) -> tuple[int, int] | None:
        remaining = []
        for p, i in self.gem_idx.items():
            if not (st.gems_mask & (1 << i)):
                remaining.append(p)
        if remaining:
            ax, ay = st.agent_pos
            remaining.sort(key=lambda p: abs(ax - p[0]) + abs(ay - p[1]))
            return remaining[0]
        return self.exit_pos

    def _heuristic_win(self, st: SearchState) -> int:
        target = self._nearest_required_target(st)
        if target is None:
            return 0
        ax, ay = st.agent_pos
        tx, ty = target
        md = abs(ax - tx) + abs(ay - ty)
        
        # With phasing, we can go through walls, so Manhattan distance is accurate
        if st.phasing_turns > 0:
            move_lb = md
        else:
            move_lb = (md + 1) // 2 if st.speed_turns > 0 else md

        remaining_gems = 0
        for _, i in self.gem_idx.items():
            if not (st.gems_mask & (1 << i)):
                remaining_gems += 1
        return move_lb + remaining_gems

    def _heuristic_pos(self, st: SearchState, target: tuple[int, int]) -> int:
        ax, ay = st.agent_pos
        tx, ty = target
        md = abs(ax - tx) + abs(ay - ty)
        
        # With phasing, walls don't block us
        if st.phasing_turns > 0:
            return md
        return (md + 1) // 2 if st.speed_turns > 0 else md

    # ---------------- A* ----------------
    def _a_star(self, start: SearchState, goal_test, heuristic, time_budget: float, max_expansions: int):
        t0 = time.time()
        open_heap = []
        counter = 0

        h0 = heuristic(start)
        heapq.heappush(open_heap, (h0, 0, counter, Node(start, g=0, h=h0)))
        counter += 1

        best_g = {start: 0}
        expansions = 0

        while open_heap:
            if expansions >= max_expansions:
                break
            if (time.time() - t0) > time_budget:
                break

            _, _, _, node = heapq.heappop(open_heap)
            if node.g != best_g.get(node.state, 10**9):
                continue

            expansions += 1

            if goal_test(node.state):
                self.last_expansions = expansions
                return self._reconstruct(node), node.state

            for action, nxt in self.get_successors(node.state):
                ng = node.g + 1
                if ng >= best_g.get(nxt, 10**9):
                    continue
                best_g[nxt] = ng
                h = heuristic(nxt)
                nn = Node(nxt, parent=node, action=action, g=ng, h=h)
                heapq.heappush(open_heap, (nn.f, nn.h, counter, nn))
                counter += 1

        self.last_expansions = expansions
        return [], None

    def _reconstruct(self, node: Node):
        out = []
        cur = node
        while cur.parent is not None:
            out.append(cur.action)
            cur = cur.parent
        out.reverse()
        return out

    # ---------------- Planning logic ----------------
    def _plan_to_win(self, gs: GridState, objective_only: bool) -> list[Action]:
        start = self.get_search_state(gs)
        if start is None or self.exit_pos is None:
            return []
        plan, _ = self._a_star(
            start,
            goal_test=self._is_goal,
            heuristic=self._heuristic_win,
            time_budget=self.time_budget_main,
            max_expansions=self.max_expansions_main
        )
        return plan

    def _choose_best_plan(self, gs: GridState, keys_held: int) -> list[Action]:
        start = self.get_search_state(gs)
        if start is None or self.exit_pos is None:
            return []

        base_plan, _ = self._a_star(
            start,
            goal_test=self._is_goal,
            heuristic=self._heuristic_win,
            time_budget=self.time_budget_main,
            max_expansions=self.max_expansions_main
        )
        if not base_plan:
            kp = self._plan_key_then_win(gs)
            return kp if kp else []

        doors_needed = self._doors_required_in_plan(base_plan)
        keys_total = keys_held + self._count_floor_keys(gs)
        if keys_total < doors_needed:
            return base_plan

        if doors_needed > 0 and keys_held == 0:
            kp = self._plan_key_then_win(gs)
            if kp:
                return kp
            return base_plan

        coin_plan = self._best_coin_harvest_then_win(gs, start, baseline_plan=base_plan, baseline_doors=doors_needed)
        if coin_plan:
            return coin_plan

        return base_plan

    def _plan_key_then_win(self, gs: GridState) -> list[Action]:
        start = self.get_search_state(gs)
        if start is None or self.exit_pos is None:
            return []

        keys = []
        for x in range(self.width):
            for y in range(self.height):
                pos = (x, y)
                for o in gs.objects_at(pos):
                    nm = getattr(o.appearance, "name", "").lower()
                    if isinstance(o, KeyEntity) or nm == "key":
                        keys.append(pos)
                        break

        best = []
        for kp in keys:
            to_key, end_st = self._a_star(
                start,
                goal_test=lambda st: st.agent_pos == kp,
                heuristic=lambda st: self._heuristic_pos(st, kp),
                time_budget=self.time_budget_eval,
                max_expansions=self.max_expansions_eval
            )
            if not to_key or end_st is None:
                continue

            picked = self._apply_key_pickup(end_st, kp)
            if picked is None:
                continue

            to_win, _ = self._a_star(
                picked,
                goal_test=self._is_goal,
                heuristic=self._heuristic_win,
                time_budget=self.time_budget_main,
                max_expansions=self.max_expansions_main
            )
            if not to_win:
                continue

            plan = to_key + [Action.PICK_UP] + to_win
            if not best or len(plan) < len(best):
                best = plan

        return best

    # ---------------- Ghost-mode minimal move ----------------
    def _greedy_objective_move(self, gs: GridState, agent_pos, speed_turns: int, phasing_turns: int) -> Action | None:
        start = self.get_search_state(gs)
        if start is None:
            return None

        target = self._nearest_required_target(start)
        if target is None:
            return None

        dist = 2 if speed_turns > 0 else 1
        has_phasing = phasing_turns > 0
        best_act = None
        best_md = 10**9

        # Prefer horizontal progress first when tied (helps “cross the wall” cases).
        ordered = []
        dx = target[0] - agent_pos[0]
        dy = target[1] - agent_pos[1]
        if abs(dx) >= abs(dy):
            ordered = [(Action.RIGHT,(1,0)), (Action.LEFT,(-1,0)), (Action.DOWN,(0,1)), (Action.UP,(0,-1))]
        else:
            ordered = [(Action.DOWN,(0,1)), (Action.UP,(0,-1)), (Action.RIGHT,(1,0)), (Action.LEFT,(-1,0))]

        for act, (mx, my) in ordered:
            cx, cy = agent_pos
            ok = True
            for _ in range(dist):
                nx, ny = cx + mx, cy + my
                if not (0 <= nx < self.width and 0 <= ny < self.height):
                    ok = False
                    break
                # With phasing, we can move through walls
                if not has_phasing and (nx, ny) in self.walls:
                    ok = False
                    break
                cx, cy = nx, ny
            if not ok:
                continue
            md = abs(cx - target[0]) + abs(cy - target[1])
            if md < best_md:
                best_md = md
                best_act = act

        return best_act

    # ---------------- Coin cluster harvest (heap tie-break fixed) ----------------
    def _select_coin_candidates(self, gs: GridState, start: SearchState) -> list[tuple[int, int]]:
        ax, ay = start.agent_pos
        scored = []
        for x in range(self.width):
            for y in range(self.height):
                pos = (x, y)
                for o in gs.objects_at(pos):
                    nm = getattr(o.appearance, "name", "").lower()
                    if isinstance(o, CoinEntity) or nm == "coin":
                        d_agent = abs(ax - x) + abs(ay - y)
                        if d_agent <= self.coin_candidate_radius:
                            scored.append((d_agent, pos))
                        break
        scored.sort()
        return [p for _, p in scored[:self.coin_candidates_k]]

    def _best_coin_harvest_then_win(self, gs: GridState, start: SearchState, baseline_plan: list[Action], baseline_doors: int) -> list[Action]:
        coins = self._select_coin_candidates(gs, start)
        if not coins:
            return []

        coin_idx = {p: i for i, p in enumerate(coins)}
        k = len(coins)

        def popcount(x: int) -> int:
            return x.bit_count()

        class HNode:
            __slots__ = ("st", "mask", "t", "parent", "act")
            def __init__(self, st, mask, t, parent, act):
                self.st = st
                self.mask = mask
                self.t = t
                self.parent = parent
                self.act = act

        def optimistic(turns_used: int, mask: int, st: SearchState) -> int:
            collected = popcount(mask)
            remaining = k - collected
            return (5 * collected - 3 * turns_used) + (5 * remaining) - 3 * self._heuristic_win(st)

        def tick_powerups(st: SearchState) -> tuple[int, int]:
            return max(0, st.speed_turns - 1), max(0, st.phasing_turns - 1)

        def coin_pick_succ(st: SearchState) -> SearchState:
            nspeed, nphase = tick_powerups(st)
            return SearchState(
                agent_pos=st.agent_pos,
                gems_mask=st.gems_mask,
                keys_mask=st.keys_mask,
                doors_open_mask=st.doors_open_mask,
                boots_mask=st.boots_mask,
                ghosts_mask=st.ghosts_mask,
                shields_mask=st.shields_mask,
                keys_held=st.keys_held,
                box_positions=st.box_positions,
                speed_turns=nspeed,
                phasing_turns=nphase,
                shield_uses=st.shield_uses,
                hp=st.hp,
            )

        def reconstruct(node: HNode) -> list[Action]:
            actions = []
            cur = node
            while cur and cur.act is not None:
                actions.append(cur.act)
                cur = cur.parent
            actions.reverse()
            return actions

        baseline_est = -3 * len(baseline_plan)

        pq = []
        push_id = 0
        root = HNode(start, 0, 0, None, None)
        heapq.heappush(pq, (-optimistic(0, 0, start), push_id, root))
        push_id += 1

        seen = set()
        best_plan = []
        best_total_est = baseline_est

        expansions = 0
        while pq and expansions < self.coin_harvest_expansions:
            _, _, node = heapq.heappop(pq)
            expansions += 1

            if node.t > self.coin_harvest_horizon:
                continue

            key = (node.st, node.mask, node.t)
            if key in seen:
                continue
            seen.add(key)

            win_actions, _ = self._a_star(
                node.st,
                goal_test=self._is_goal,
                heuristic=self._heuristic_win,
                time_budget=self.time_budget_eval,
                max_expansions=self.max_expansions_eval
            )

            if win_actions:
                coins_collected = popcount(node.mask)
                total_est = (5 * coins_collected - 3 * node.t) + (-3 * len(win_actions))
                if total_est > best_total_est:
                    best_total_est = total_est
                    best_plan = reconstruct(node) + win_actions

            for act, nxt in self.get_successors(node.st):
                if act == Action.PICK_UP:
                    if nxt.gems_mask != node.st.gems_mask:
                        heapq.heappush(pq, (-optimistic(node.t + 1, node.mask, nxt), push_id,
                                            HNode(nxt, node.mask, node.t + 1, node, act)))
                        push_id += 1
                    continue

                heapq.heappush(pq, (-optimistic(node.t + 1, node.mask, nxt), push_id,
                                    HNode(nxt, node.mask, node.t + 1, node, act)))
                push_id += 1

            pos = node.st.agent_pos
            if pos in coin_idx:
                i = coin_idx[pos]
                if not (node.mask & (1 << i)):
                    nxt_st = coin_pick_succ(node.st)
                    nxt_mask = node.mask | (1 << i)
                    heapq.heappush(pq, (-optimistic(node.t + 1, nxt_mask, nxt_st), push_id,
                                        HNode(nxt_st, nxt_mask, node.t + 1, node, Action.PICK_UP)))
                    push_id += 1

        return best_plan

    # ---------------- Apply pickups for key-first planning ----------------
    def _apply_key_pickup(self, st: SearchState, pos: tuple[int, int]) -> SearchState | None:
        nspeed = max(0, st.speed_turns - 1)
        nphase = max(0, st.phasing_turns - 1)
        new_keys_mask = st.keys_mask
        if pos in self.key_idx:
            i = self.key_idx[pos]
            new_keys_mask |= (1 << i)
        return SearchState(
            agent_pos=st.agent_pos,
            gems_mask=st.gems_mask,
            keys_mask=new_keys_mask,
            doors_open_mask=st.doors_open_mask,
            boots_mask=st.boots_mask,
            ghosts_mask=st.ghosts_mask,
            shields_mask=st.shields_mask,
            keys_held=st.keys_held + 1,
            box_positions=st.box_positions,
            speed_turns=nspeed,
            phasing_turns=nphase,
            shield_uses=st.shield_uses,
            hp=st.hp,
        )

    # ---------------- Successors (accurate mechanics) ----------------
    def get_successors(self, st: SearchState):
        succs = []
        x, y = st.agent_pos
        has_speed = st.speed_turns > 0
        has_phasing = st.phasing_turns > 0

        def tick(speed, phasing):
            return max(0, speed - 1), max(0, phasing - 1)

        box_set = set(st.box_positions)

        def door_is_locked(pos) -> bool:
            if pos not in self.door_idx:
                return False
            idx = self.door_idx[pos]
            return not (st.doors_open_mask & (1 << idx))

        def can_occupy(pos) -> bool:
            if has_phasing:
                return True
            if pos in self.walls:
                return False
            if door_is_locked(pos):
                return False
            if pos in box_set:
                return False
            return True

        def can_push(box_pos, dx, dy):
            px, py = box_pos[0] + dx, box_pos[1] + dy
            ppos = (px, py)
            if not (0 <= px < self.width and 0 <= py < self.height):
                return False, None
            if ppos in self.walls:
                return False, None
            if ppos in box_set:
                return False, None
            if ppos in self.lava_positions:
                return False, None
            if ppos in self.door_idx:
                return False, None
            return True, ppos

        def apply_lava(pos, hp, shield_uses):
            if has_phasing:
                return hp, shield_uses
            if pos not in self.lava_positions:
                return hp, shield_uses
            if shield_uses > 0:
                return hp, shield_uses - 1
            return hp - 2, shield_uses

        moves = {
            Action.UP: (0, -1),
            Action.DOWN: (0, 1),
            Action.LEFT: (-1, 0),
            Action.RIGHT: (1, 0),
        }
        
        # When phasing is active, prioritize moves that enter unreachable regions
        # This ensures we use phasing turns to break into normally-inaccessible areas
        if has_phasing:
            cx, cy = st.agent_pos
            
            def enters_unreachable_region(dx, dy) -> int:
                """
                Returns priority score based on whether move enters a region
                that's unreachable without phasing.
                This is the KEY insight: phasing should be used to access new regions.
                """
                score = 0
                nx, ny = cx + dx, cy + dy
                npos = (nx, ny)
                
                # HUGE bonus if this move takes us somewhere we can't normally reach
                if npos not in self.reachable_no_phase:
                    score += 1000  # Massive priority for entering unreachable region
                
                return score
            
            # Order moves by: (1) unreachable region score, (2) Manhattan distance reduction
            move_list = []
            for act, (dx, dy) in moves.items():
                unreachable_score = enters_unreachable_region(dx, dy)
                
                # Also calculate distance reduction as secondary criterion
                if self.exit_pos is not None:
                    ex, ey = self.exit_pos
                    new_dist = abs(cx + dx - ex) + abs(cy + dy - ey)
                    current_dist = abs(cx - ex) + abs(cy - ey)
                    dist_reduction = current_dist - new_dist
                else:
                    dist_reduction = 0
                
                # Prioritize: (1) entering unreachable region, (2) distance reduction
                move_list.append((unreachable_score, dist_reduction, act, (dx, dy)))
            
            # Sort by unreachable score first (descending), then distance reduction (descending)
            move_list.sort(key=lambda x: (-x[0], -x[1]))
            moves_ordered = [(act, delta) for (_, _, act, delta) in move_list]
        else:
            moves_ordered = list(moves.items())

        # Movement
        for act, (dx, dy) in moves_ordered:
            dist = 2 if has_speed else 1

            cur_pos = st.agent_pos
            cur_boxes = list(st.box_positions)
            cur_box_set = set(cur_boxes)
            cur_hp = st.hp
            cur_shield = st.shield_uses

            moved_any = False

            for _ in range(dist):
                nx, ny = cur_pos[0] + dx, cur_pos[1] + dy
                npos = (nx, ny)
                if not (0 <= nx < self.width and 0 <= ny < self.height):
                    break

                if (not has_phasing) and (npos in cur_box_set):
                    ok, pushed_to = can_push(npos, dx, dy)
                    if not ok:
                        break
                    bi = cur_boxes.index(npos)
                    cur_boxes[bi] = pushed_to
                    cur_boxes.sort()
                    cur_box_set = set(cur_boxes)
                    cur_pos = npos
                    moved_any = True
                else:
                    if not can_occupy(npos):
                        break
                    cur_pos = npos
                    moved_any = True

                cur_hp, cur_shield = apply_lava(cur_pos, cur_hp, cur_shield)
                if cur_hp <= 0:
                    moved_any = False
                    break

            if not moved_any:
                continue

            nspeed, nphase = tick(st.speed_turns, st.phasing_turns)
            succs.append((act, SearchState(
                agent_pos=cur_pos,
                gems_mask=st.gems_mask,
                keys_mask=st.keys_mask,
                doors_open_mask=st.doors_open_mask,
                boots_mask=st.boots_mask,
                ghosts_mask=st.ghosts_mask,
                shields_mask=st.shields_mask,
                keys_held=st.keys_held,
                box_positions=tuple(cur_boxes),
                speed_turns=nspeed,
                phasing_turns=nphase,
                shield_uses=cur_shield,
                hp=cur_hp,
            )))

        # Pickups we model: gems/keys/powerups
        nspeed, nphase = tick(st.speed_turns, st.phasing_turns)

        # Gem pickup
        if st.agent_pos in self.gem_idx:
            i = self.gem_idx[st.agent_pos]
            if not (st.gems_mask & (1 << i)):
                succs.append((Action.PICK_UP, SearchState(
                    agent_pos=st.agent_pos,
                    gems_mask=st.gems_mask | (1 << i),
                    keys_mask=st.keys_mask,
                    doors_open_mask=st.doors_open_mask,
                    boots_mask=st.boots_mask,
                    ghosts_mask=st.ghosts_mask,
                    shields_mask=st.shields_mask,
                    keys_held=st.keys_held,
                    box_positions=st.box_positions,
                    speed_turns=nspeed,
                    phasing_turns=nphase,
                    shield_uses=st.shield_uses,
                    hp=st.hp,
                )))

        # Key pickup
        if st.agent_pos in self.key_idx:
            i = self.key_idx[st.agent_pos]
            if not (st.keys_mask & (1 << i)):
                succs.append((Action.PICK_UP, SearchState(
                    agent_pos=st.agent_pos,
                    gems_mask=st.gems_mask,
                    keys_mask=st.keys_mask | (1 << i),
                    doors_open_mask=st.doors_open_mask,
                    boots_mask=st.boots_mask,
                    ghosts_mask=st.ghosts_mask,
                    shields_mask=st.shields_mask,
                    keys_held=st.keys_held + 1,
                    box_positions=st.box_positions,
                    speed_turns=nspeed,
                    phasing_turns=nphase,
                    shield_uses=st.shield_uses,
                    hp=st.hp,
                )))

        # Boots pickup
        if st.agent_pos in self.boots_idx:
            i = self.boots_idx[st.agent_pos]
            if not (st.boots_mask & (1 << i)):
                succs.append((Action.PICK_UP, SearchState(
                    agent_pos=st.agent_pos,
                    gems_mask=st.gems_mask,
                    keys_mask=st.keys_mask,
                    doors_open_mask=st.doors_open_mask,
                    boots_mask=st.boots_mask | (1 << i),
                    ghosts_mask=st.ghosts_mask,
                    shields_mask=st.shields_mask,
                    keys_held=st.keys_held,
                    box_positions=st.box_positions,
                    speed_turns=5,
                    phasing_turns=nphase,
                    shield_uses=st.shield_uses,
                    hp=st.hp,
                )))

        # Ghost pickup
        if st.agent_pos in self.ghosts_idx:
            i = self.ghosts_idx[st.agent_pos]
            if not (st.ghosts_mask & (1 << i)):
                succs.append((Action.PICK_UP, SearchState(
                    agent_pos=st.agent_pos,
                    gems_mask=st.gems_mask,
                    keys_mask=st.keys_mask,
                    doors_open_mask=st.doors_open_mask,
                    boots_mask=st.boots_mask,
                    ghosts_mask=st.ghosts_mask | (1 << i),
                    shields_mask=st.shields_mask,
                    keys_held=st.keys_held,
                    box_positions=st.box_positions,
                    speed_turns=nspeed,
                    phasing_turns=5,
                    shield_uses=st.shield_uses,
                    hp=st.hp,
                )))

        # Shield pickup
        if st.agent_pos in self.shields_idx:
            i = self.shields_idx[st.agent_pos]
            if not (st.shields_mask & (1 << i)):
                succs.append((Action.PICK_UP, SearchState(
                    agent_pos=st.agent_pos,
                    gems_mask=st.gems_mask,
                    keys_mask=st.keys_mask,
                    doors_open_mask=st.doors_open_mask,
                    boots_mask=st.boots_mask,
                    ghosts_mask=st.ghosts_mask,
                    shields_mask=st.shields_mask | (1 << i),
                    keys_held=st.keys_held,
                    box_positions=st.box_positions,
                    speed_turns=nspeed,
                    phasing_turns=nphase,
                    shield_uses=5,
                    hp=st.hp,
                )))

        # USE_KEY
        if st.keys_held > 0:
            nspeed, nphase = tick(st.speed_turns, st.phasing_turns)
            for dx, dy in ((0, 1), (0, -1), (1, 0), (-1, 0)):
                p = (x + dx, y + dy)
                if p in self.door_idx:
                    i = self.door_idx[p]
                    if not (st.doors_open_mask & (1 << i)):
                        succs.append((Action.USE_KEY, SearchState(
                            agent_pos=st.agent_pos,
                            gems_mask=st.gems_mask,
                            keys_mask=st.keys_mask,
                            doors_open_mask=st.doors_open_mask | (1 << i),
                            boots_mask=st.boots_mask,
                            ghosts_mask=st.ghosts_mask,
                            shields_mask=st.shields_mask,
                            keys_held=st.keys_held - 1,
                            box_positions=st.box_positions,
                            speed_turns=nspeed,
                            phasing_turns=nphase,
                            shield_uses=st.shield_uses,
                            hp=st.hp,
                        )))

        return succs

    # ---------------- Runtime feasibility + fallback ----------------
    def _action_feasible_now(self, gs: GridState, action: Action) -> bool:
        if action in (Action.PICK_UP, Action.USE_KEY, Action.WAIT):
            return True

        agent_pos = self.find_agent_pos(gs)
        if agent_pos is None:
            return False

        agent_obj = self._find_agent_obj(gs, agent_pos)
        _, _, speed_turns, phasing_turns, _ = (5, 0, 0, 0, 0)
        if agent_obj is not None:
            _, _, speed_turns, phasing_turns, _ = self._read_agent_status(agent_obj)

        # Apply shadow phasing here too, so we don't reject wall-crossing moves mid-ghost.
        if phasing_turns == 0 and self._phasing_shadow_turns > 0:
            phasing_turns = self._phasing_shadow_turns

        has_phasing = phasing_turns > 0
        dist = 2 if speed_turns > 0 else 1

        moves = {Action.UP:(0,-1), Action.DOWN:(0,1), Action.LEFT:(-1,0), Action.RIGHT:(1,0)}
        dx, dy = moves[action]

        cx, cy = agent_pos
        for _ in range(dist):
            nx, ny = cx + dx, cy + dy
            if not (0 <= nx < self.width and 0 <= ny < self.height):
                return False
            npos = (nx, ny)

            if not has_phasing:
                if npos in self.walls:
                    return False
                if self._is_locked_door_cell(gs, npos):
                    return False
                box_here = self._cell_has(gs, npos, BoxEntity, "box")
                if box_here:
                    px, py = nx + dx, ny + dy
                    if not (0 <= px < self.width and 0 <= py < self.height):
                        return False
                    ppos = (px, py)
                    if ppos in self.walls:
                        return False
                    if self._is_locked_door_cell(gs, ppos):
                        return False
                    if self._cell_has(gs, ppos, BoxEntity, "box"):
                        return False
                    if ppos in self.lava_positions:
                        return False

            cx, cy = nx, ny

        return True

    def _fallback_move(self, gs: GridState, agent_pos, hp: int, shield_uses: int) -> Action | None:
        candidates = [Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT]
        best = None
        best_pen = 10**9

        for act in candidates:
            if act not in (Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT):
                continue
            npos = self._next_pos_for_move(agent_pos, act)
            nx, ny = npos
            if not (0 <= nx < self.width and 0 <= ny < self.height):
                continue
            if npos in self.walls:
                continue
            if self._is_locked_door_cell(gs, npos):
                continue

            pen = 0
            if npos in self.lava_positions and shield_uses == 0 and hp <= 2:
                pen += 1000

            if pen < best_pen:
                best_pen = pen
                best = act

        return best
