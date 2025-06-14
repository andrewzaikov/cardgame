package ru.labs.game.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class CardGameClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGameClientApp.class.getResource("/ru/labs/game/main-form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CardGameClientController controller = fxmlLoader.getController();

        controller.start(stage, scene);

        stage.setTitle("21 point (card game)");
        stage.getIcons().add(new Image("/poker_icon.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}