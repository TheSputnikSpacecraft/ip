# AI Tool Usage and Observations

This document records the use of AI tools during the development of this project, including what they were used for and reflections on their effectiveness.

---

## Usage Record

| Increment | Tool Used | Purpose |
|-----------|-----------|---------|
| Level 2–6 | Google DeepMind Agentic AI | Generating initial skeletal classes and basic OOP structure. |
| A-Collections | Google DeepMind Agentic AI | Refactoring task storage from arrays to `ArrayList`. |
| A-Enums | Google DeepMind Agentic AI | Refactoring constants in `AK.java` into enums. |
| Level-8 (Dates & Times) | AI Tools | Assisting with parsing logic, `LocalDate` usage, and debugging date-related errors. |
| A-MoreOOP | AI Tools | Suggesting improvements to class responsibilities and abstraction. |
| A-Packages | AI Tools | Advising on package organization and restructuring classes logically. |
| A-Gradle | AI Tools | Clarifying Gradle configuration and build setup. |
| A-JUnit | AI Tools | Assisting in writing unit test skeletons and debugging test failures. |
| A-Jar | AI Tools | Verifying JAR configuration and troubleshooting packaging issues. |
| A-JavaDoc | AI Tools | Generating and refining JavaDoc comments for classes and methods. |
| A-CodingStandard | AI Tools | Reviewing code for compliance with Java coding standards. |
| Level-9 (FindDuke) | AI Tools | Debugging search logic and refining filtering behavior. |
| Level-10 (GUI) | AI Tools | Debugging JavaFX-related issues (official JavaFX tutorial instructions were followed for configuration). |
| A-CodeQuality | AI Tools | Identifying areas for refactoring and improving readability. |
| A-Streams (optional) | AI Tools | Suggesting stream-based refactors where applicable. |
| A-CI (optional) | AI Tools | Guidance on setting up GitHub Actions workflow. |

---

## Observations

### What Worked Well

- **Rapid prototyping**: AI significantly reduced setup time by generating boilerplate code, class scaffolding, and test skeletons.
- **Refactoring assistance**: Structural refactors (arrays → `ArrayList`, constants → enums, stream suggestions) were handled efficiently.
- **JavaDoc generation**: AI was effective in drafting clear documentation comments, which were later refined manually.
- **Debugging support**: Helpful in identifying logical errors, especially in date parsing, filtering logic, and JUnit failures.
- **Build tooling clarification**: Useful for understanding Gradle configuration, JAR packaging, and CI setup.

### What Required Manual Oversight

- **Edge case handling**: AI-generated logic occasionally missed assignment-specific constraints.
- **Overgeneralized suggestions**: Some refactors were broader than necessary and had to be trimmed to remain within Duke project scope.
- **Integration issues**: Certain AI-generated solutions required adjustments to match existing architecture.
- **GUI configuration**: For Level-10, official JavaFX tutorial instructions were prioritized to avoid inconsistent configurations.

---

## Overall Impact

- AI tools noticeably reduced development time, especially during scaffolding, refactoring, documentation writing, and debugging.
- Final correctness, design decisions, architectural consistency, and compliance with assignment requirements required human review.
- AI served as a development assistant rather than a replacement for understanding or design judgment.

---

This document will be updated periodically as AI tools continue to be used throughout the project.
