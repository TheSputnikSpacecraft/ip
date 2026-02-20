package goldexperiencerequiem;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image requiemImage = new Image(getClass().getResourceAsStream("/images/DaDuke.png")); // rename file
                                                                                                        // later if you
                                                                                                        // want

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

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String response = requiem.getResponse(input);
        boolean isError = response.trim().startsWith("You ain't makin' sense");

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, requiemImage, isError));

        userInput.clear();

        if (requiem.isExitCommand()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.millis(800));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
