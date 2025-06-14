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
import ru.labs.game.model.Card;
import ru.labs.game.model.CardSuit;
import ru.labs.game.rest.Client;
import ru.labs.game.service.Engine;
import ru.labs.game.service.GameStatus;

import java.util.Random;
import java.util.UUID;

public class CardGameClientController {
    private final Random random = new Random(System.currentTimeMillis());
    private final CardSuit[] suits = {CardSuit.HEARTS, CardSuit.DIAMOND, CardSuit.CLUBS, CardSuit.SPADES};
    @FXML
    private Label buttonHelpText;
    @FXML
    private Label opponentCardsLabel;
    @FXML
    private Label myCardsLabel;
    @FXML
    private FlowPane opponentCards;
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
    @FXML
    private Label messagesLabel;

    private Stage stage;
    private Scene scene;

    public void start(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;

        updateState();
    }

    private void updateState() {
        myCards.getChildren().clear();
        if (Engine.getMyCards().isEmpty()) {
            myCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }

        opponentCards.getChildren().clear();
        if (Engine.getOpponentCards().isEmpty()) {
            opponentCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }

        if (Client.isConnected()) {
            connectToServerItem.setDisable(true);
            stopGameItem.setDisable(false);
            joinGameItem.setDisable(Engine.getStatus() == GameStatus.WAIT_OPPONENT_CONNECTION);

            for (Card card : Engine.getMyCards()) {
                myCards.getChildren().add(createImageViewWithCard(card));
            }
            for (Card card : Engine.getOpponentCards()) {
                opponentCards.getChildren().add(createImageViewWithCard(card));
            }

            opponentCardsLabel.setText("Cards of your opponent, score = " + Engine.getScore(Engine.getOpponentCards()));
            myCardsLabel.setText("Your cards, score = " + Engine.getScore(Engine.getMyCards()));

            takeCardButton.setDisable(false);
            takeCardItem.setDisable(false);
            passMoveButton.setDisable(false);
            passMoveItem.setDisable(false);

            switch (Engine.getStatus()) {
                case WAIT_OPPONENT_CONNECTION -> messagesLabel.setText("Waiting, when opponent is connected...");
                case OPPONENTS_TURN -> messagesLabel.setText("Please wait! Now your opponent is moving.");
                case PLAYER_TURN -> messagesLabel.setText("Make your move!");
                case PLAYER_WON -> messagesLabel.setText("CONGRATULATIONS!!! You've won!!!");
                case PLAYER_LOST -> messagesLabel.setText("You've lost. Don't be sad!");
            }
        } else {
            connectToServerItem.setDisable(false);
            joinGameItem.setDisable(true);
            stopGameItem.setDisable(true);
            opponentCardsLabel.setText("Cards of your opponent:");
            myCardsLabel.setText("Your cards:");

            takeCardButton.setDisable(true);
            takeCardItem.setDisable(true);
            passMoveButton.setDisable(true);
            passMoveItem.setDisable(true);

            messagesLabel.setText("Please, connect to server.");
        }
    }

    private static ImageView createImageViewWithCard(Card card) {
        return new ImageView(new Image(Card.getResourceLocation(card.suit(), card.value())));
    }

    @FXML
    protected void onTakeCardClick() {
        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        Engine.getMyCards().add(new Card(value, suit));

        updateState();
    }

    @FXML
    protected void onPassMoveClick() {
        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        Engine.getOpponentCards().add(new Card(value, suit));

        updateState();
    }

    @FXML
    protected void onExitApplication() {
        System.exit(0);
    }

    @FXML
    protected void onAboutAction() {
        //TODO: show about dialogue
    }

    @FXML
    protected void onServerConnect() {
        //todo: server connection dialogue

        Engine.initGame();
        Client.setToken(UUID.randomUUID().toString());
        updateState();
    }

    @FXML
    protected void onJoinGame() {
        //todo: join a game dialogue
    }

    @FXML
    protected void onStopGame() {
        //todo: stop current game

        Engine.initGame();
        Client.disconnect();
        updateState();
    }
}