package ru.labs.game.app;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.javatuples.Pair;
import ru.labs.game.model.Card;
import ru.labs.game.model.CardSuit;
import ru.labs.game.rest.Client;
import ru.labs.game.service.Engine;
import ru.labs.game.service.GameStatus;

import java.util.Random;

public class CardGameClientController {
    private final Random random = new Random(System.currentTimeMillis());
    private final CardSuit[] suits = {CardSuit.HEARTS, CardSuit.DIAMOND, CardSuit.CLUBS, CardSuit.SPADES};
    @FXML
    private Label welcomeText;
    @FXML
    private Label enemyCardsLabel;
    @FXML
    private Label myCardsLabel;
    @FXML
    private FlowPane enemyCards;
    @FXML
    private FlowPane myCards;
    @FXML
    private MenuItem connectToServerItem;
    @FXML
    private MenuItem joinGameItem;
    @FXML
    private MenuItem stopGameItem;
    @FXML
    private MenuItem takeCardItem;
    @FXML
    private MenuItem passMoveItem;
    @FXML
    private Button takeCardButton;
    @FXML
    private Button passMoveButton;
    private Stage stage;
    private Scene scene;

    public void start(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;

        updateState();
    }

    private void updateState() {
        enemyCards.getChildren().removeAll();
        myCards.getChildren().removeAll();

        if (Client.isConnected()) {
            connectToServerItem.setDisable(true);
            stopGameItem.setDisable(false);
            if (Engine.getStatus() == GameStatus.WAIT_OPPONENT_CONNECTION) {
                joinGameItem.setDisable(true);
            } else {
                joinGameItem.setDisable(false);
            }

            int myScore = 0;
            for (var item : Engine.getMyCards()) {
                myCards.getChildren().add(new ImageView(new Image(Card.getResourceLocation(item.suit(), item.value()))));
                myScore += item.value();
            }

            int enemyScore = 0;
            for (var item : Engine.getEnemyCards()) {
                enemyCards.getChildren().add(new ImageView(new Image(Card.getResourceLocation(item.suit(), item.value()))));
                enemyScore += item.value();
            }
        } else {
            connectToServerItem.setDisable(false);
            joinGameItem.setDisable(true);
            stopGameItem.setDisable(true);
        }
    }

    @FXML
    protected void onTakeCardClick() {
        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        String location = Card.getResourceLocation(suit, value);
        if (location != null) {
            myCards.getChildren().add(new ImageView(new Image(location)));
        }
    }

    @FXML
    protected void onPassMoveClick() {
        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        String location = Card.getResourceLocation(suit, value);
        if (location != null) {
            enemyCards.getChildren().add(new ImageView(new Image(location)));
        }
    }

    @FXML
    protected void onExitApplication() {
        System.exit(0);
    }

    @FXML
    protected void onAboutAction() {
        //TODO: show about dialogue
    }
}