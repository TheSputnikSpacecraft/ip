package goldexperiencerequiem;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/requiem.txt";
    private Requiem requiem;

    public Main(String filePath) {
        this.requiem = new Requiem(filePath);
    }

    public Main() {
        this(DEFAULT_FILE_PATH);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setRequiem(requiem);  // inject the Duke instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
