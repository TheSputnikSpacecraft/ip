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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println("____________________________________________________________");
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(" added: " + input);
                System.out.println("____________________________________________________________");
            }
        }

        scanner.close();
    }
}
