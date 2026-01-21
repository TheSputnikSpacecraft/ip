import java.util.Scanner;
import java.util.ArrayList;

/**
 * Requiem is a personal assistant chatbot.
 * It currently supports greeting, echoing user input, and exiting.
 */
public class Requiem {
    /**
     * Entry point of the application.
     * Reads user input and echoes it back until the "bye" command is received.
     *
     * @param args (not used).
     */
    public static void main(String[] args) {
        String greeting = "____________________________________________________________\n" +
                " Hello! I'm Requiem\n" +
                " What can I do for you?\n" +
                "____________________________________________________________";
        String exit = " Bye. Hope to see you again soon!\n" +
                "____________________________________________________________";

        System.out.println(greeting);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            System.out.println("____________________________________________________________");

            try {
                String[] words = input.split(" ", 2);
                Command command;
                try {
                    command = Command.valueOf(words[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RequiemException("I'm sorry, but I don't know what that means :-(");
                }

                switch (command) {
                    case BYE:
                        System.out.println(exit);
                        scanner.close();
                        return;
                    case LIST:
                        System.out.println(" Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(" " + (i + 1) + "." + tasks.get(i));
                        }
                        System.out.println("____________________________________________________________");
                        break;
                    case MARK:
                        if (words.length < 2)
                            throw new RequiemException("You need to specify a task index to mark.");
                        int markIndex = Integer.parseInt(words[1]) - 1;
                        tasks.get(markIndex).markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks.get(markIndex));
                        System.out.println("____________________________________________________________");
                        break;
                    case UNMARK:
                        if (words.length < 2)
                            throw new RequiemException("You need to specify a task index to unmark.");
                        int unmarkIndex = Integer.parseInt(words[1]) - 1;
                        tasks.get(unmarkIndex).markAsUndone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks.get(unmarkIndex));
                        System.out.println("____________________________________________________________");
                        break;
                    case TODO:
                        if (words.length < 2 || words[1].trim().isEmpty()) {
                            throw new RequiemException("The description of a todo cannot be empty.");
                        }
                        Task todo = new Todo(words[1].trim());
                        tasks.add(todo);
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + todo);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println("____________________________________________________________");
                        break;
                    case DEADLINE:
                        if (words.length < 2 || words[1].trim().isEmpty()) {
                            throw new RequiemException("The description of a deadline cannot be empty.");
                        }
                        int byIndex = input.indexOf("/by");
                        if (byIndex == -1) {
                            throw new RequiemException("The deadline must have a /by time.");
                        }
                        String deadlineDesc = input.substring(9, byIndex).trim();
                        if (deadlineDesc.isEmpty()) {
                            throw new RequiemException("The description of a deadline cannot be empty.");
                        }
                        String by = input.substring(byIndex + 3).trim();
                        Task deadline = new Deadline(deadlineDesc, by);
                        tasks.add(deadline);
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + deadline);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println("____________________________________________________________");
                        break;
                    case EVENT:
                        if (words.length < 2 || words[1].trim().isEmpty()) {
                            throw new RequiemException("The description of an event cannot be empty.");
                        }
                        int fromIndex = input.indexOf("/from");
                        int toIndex = input.indexOf("/to");
                        if (fromIndex == -1 || toIndex == -1) {
                            throw new RequiemException("The event must have /from and /to times.");
                        }
                        String eventDesc = input.substring(6, fromIndex).trim();
                        if (eventDesc.isEmpty()) {
                            throw new RequiemException("The description of an event cannot be empty.");
                        }
                        String from = input.substring(fromIndex + 5, toIndex).trim();
                        String to = input.substring(toIndex + 3).trim();
                        Task event = new Event(eventDesc, from, to);
                        tasks.add(event);
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + event);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println("____________________________________________________________");
                        break;
                    case DELETE:
                        if (words.length < 2)
                            throw new RequiemException("You need to specify a task index to delete.");
                        int deleteIndex = Integer.parseInt(words[1]) - 1;
                        Task removedTask = tasks.remove(deleteIndex);
                        System.out.println(" Noted. I've removed this task:");
                        System.out.println("   " + removedTask);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println("____________________________________________________________");
                        break;
                }
            } catch (RequiemException e) {
                System.out.println(" Yare Yare Daze " + e.getMessage());
                System.out.println("____________________________________________________________");
            } catch (Exception e) { // Catch-all for other errors like number format
                System.out.println(" WRRYYYYYY!!! " + e.getMessage());
                System.out.println("____________________________________________________________");
            }
        }

    }
}
