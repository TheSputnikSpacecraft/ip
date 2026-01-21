import java.util.Scanner;

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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            System.out.println("____________________________________________________________");

            if (input.equals("bye")) {
                System.out.println(exit);
                break;
            } else if (input.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks[index].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[index]);
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks[index].markAsUndone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[index]);
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("deadline ")) {
                int byIndex = input.indexOf("/by");
                String description = input.substring(9, byIndex).trim();
                String by = input.substring(byIndex + 3).trim();
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
                System.out.println("____________________________________________________________");
            } else if (input.startsWith("event ")) {
                int fromIndex = input.indexOf("/from");
                int toIndex = input.indexOf("/to");
                String description = input.substring(6, fromIndex).trim();
                String from = input.substring(fromIndex + 5, toIndex).trim();
                String to = input.substring(toIndex + 3).trim();
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks[taskCount - 1]);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
                System.out.println("____________________________________________________________");
            } else {
                // Fallback
                System.out.println(" OOPS!!! I'm sorry, but I don't know what that means :-(");
                System.out.println("____________________________________________________________");
            }
        }

        scanner.close();
    }
}
