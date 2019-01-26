package Lesson_6.Client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MiniStage extends Stage {
    public MiniStage() throws IOException {
        Parent root = null;
        root = FXMLLoader.load(getClass().getResource("secondWindow.fxml"));
        setTitle("second Window!");
        Scene scene = new Scene(root, 250, 450);
        setScene(scene);
    }
}
