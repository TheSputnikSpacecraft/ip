package goldexperiencerequiem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Requiem requiem;

    private final Image userImage =
            new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image requiemImage =
            new Image(getClass().getResourceAsStream("/images/DaDuke.png")); // rename file later if you want

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Requiem instance */
    public void setRequiem(Requiem requiem) {
        this.requiem = requiem;
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        // optional: ignore empty input so you don't spam blank bubbles
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String response = requiem.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, requiemImage) // can rename method later
        );

        userInput.clear();
    }
}
