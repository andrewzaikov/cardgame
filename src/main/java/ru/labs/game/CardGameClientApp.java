package ru.labs.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.labs.game.app.CardGameClientController;
import ru.labs.game.rest.Client;
import ru.labs.game.service.Game;

import java.io.IOException;

@SpringBootApplication(proxyBeanMethods=false)
public class CardGameClientApp extends Application {
    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardGameClientApp.class.getResource("/ru/labs/game/main-form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CardGameClientController controller = fxmlLoader.getController();

        //todo: not beautiful decision...
        Client restClient = applicationContext.getBean(Client.class);
        Game gameEngine = applicationContext.getBean(Game.class);
        controller.setGameEngine(gameEngine);
        controller.setRestClient(restClient);

        controller.start(stage, scene);

        stage.setTitle("21 point (card game)");
        stage.getIcons().add(new Image("/poker_icon.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(CardGameClientApp.class, args);
        launch();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}