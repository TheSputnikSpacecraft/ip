package goldexperiencerequiem;

/**
 * Represents a todo task.
 */
public class Todo extends Task {

     private static final String TODO_TYPE_ICON = "T";
     private static final String FILE_DELIMITER = " | ";

     /**
      * Creates a new Todo task with the given description.
      *
      * @param description The description of the todo.
      */
     public Todo(String description) {
          super(description);
     }

     /**
      * Returns the string representation of the todo task.
      *
      * @return The todo task icon, status icon, and description.
      */
     @Override
     public String toString() {
          return "[" + TODO_TYPE_ICON + "]" + super.toString();
     }

     /**
      * Returns the file-friendly string representation of the todo task.
      *
      * @return File format string.
      */
     @Override
     public String toFileFormat() {
          return TODO_TYPE_ICON + FILE_DELIMITER + super.toFileFormat();
     }
}
// INDENT FIX
