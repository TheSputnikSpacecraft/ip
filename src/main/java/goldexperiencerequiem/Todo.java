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

     /**
      * Checks if this Todo object is equal to another object.
      * Equality is determined by the superclass's equals method, as Todo has no
      * additional fields
      * that affect its identity beyond what Task already defines.
      *
      * @param obj The object to compare with.
      * @return True if the objects are equal, false otherwise.
      */
     @Override
     public boolean equals(Object obj) {
          // Todo uses super.equals which checks description, so explicit override isn't
          // strictly necessary
          // but good for consistency/clarity if we add fields later.
          // For now, super.equals is sufficient as Todo has no extra fields.
          return super.equals(obj);
     }

     /**
      * Returns a hash code value for the Todo object.
      * The hash code is based on the superclass's hash code, as Todo has no
      * additional fields
      * that affect its identity beyond what Task already defines.
      *
      * @return A hash code value for this object.
      */
     @Override
     public int hashCode() {
          return super.hashCode();
     }
}
// INDENT FIX
