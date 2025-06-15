package ru.labs.game.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.labs.game.model.Card;
import ru.labs.game.model.CardSuit;
import ru.labs.game.rest.Client;
import ru.labs.game.rest.GameInfoDto;
import ru.labs.game.rest.GameListItemDto;
import ru.labs.game.rest.StatusDto;
import ru.labs.game.service.Game;
import ru.labs.game.service.GameStatus;

import java.util.*;

public class CardGameClientController {
    //private final Random random = new Random(System.currentTimeMillis());
    //private final CardSuit[] suits = {CardSuit.HEARTS, CardSuit.DIAMOND, CardSuit.CLUBS, CardSuit.SPADES};
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

    //private Stage stage;
    //private Scene scene;

    private static ImageView createImageViewWithCard(Card card, boolean hideCard) {
        String resource = "/suit.jpg";
        if (!hideCard) {
            resource = Card.getResourceLocation(card.suit(), card.value());
        }
        return new ImageView(new Image(resource));
    }

    public void start(Stage stage, Scene scene) {
        //this.stage = stage;
        //this.scene = scene;

        updateState();
    }

    private void updateState() {
        myCards.getChildren().clear();
        opponentCards.getChildren().clear();

        if (Client.isConnected()) {
            GameInfoDto gameInfoDto = Client.getInfo();
            Game.updateState(gameInfoDto);

            if (gameInfoDto.status() == StatusDto.SESSION_CLOSED) {
                Client.disconnect();

            } else {
                connectToServerItem.setDisable(true);
                stopGameItem.setDisable(false);
                joinGameItem.setDisable(Game.getStatus() != GameStatus.CONNECTED);

                for (Card card : Game.getMyCards()) {
                    myCards.getChildren().add(createImageViewWithCard(card, false));
                }
                boolean hideCard = (gameInfoDto.status() == StatusDto.PLAYER_MOVE || gameInfoDto.status() == StatusDto.OPPONENT_MOVE);
                for (Card card : Game.getOpponentCards()) {
                    opponentCards.getChildren().add(createImageViewWithCard(card, hideCard));
                }

                opponentCardsLabel.setText("Cards of your opponent, score =" + (hideCard ? " ?" : " "+ Game.getScore(Game.getOpponentCards())));
                myCardsLabel.setText("Your cards, score = " + Game.getScore(Game.getMyCards()));

                takeCardButton.setDisable(false);
                takeCardItem.setDisable(false);
                passMoveButton.setDisable(false);
                passMoveItem.setDisable(false);

                switch (Game.getStatus()) {
                    case CONNECTED -> messagesLabel.setText("Now join an existing game.");
                    case WAIT_OPPONENT_CONNECTION -> messagesLabel.setText("Waiting, when opponent is connected...");
                    case OPPONENTS_TURN -> messagesLabel.setText("Please wait! Now your opponent is moving.");
                    case PLAYER_TURN -> messagesLabel.setText("Make your move!");
                    case PLAYER_WON -> messagesLabel.setText("CONGRATULATIONS!!! You've won!!!");
                    case PLAYER_LOST -> messagesLabel.setText("You've lost. Don't be sad.");
                    case DRAW -> messagesLabel.setText("Draw! You all are the champions!");
                }
            }
        }

        if (!Client.isConnected()) {
            connectToServerItem.setDisable(false);
            joinGameItem.setDisable(true);
            stopGameItem.setDisable(true);
            opponentCardsLabel.setText("Cards of your opponent:");
            myCardsLabel.setText("Your cards:");

            takeCardButton.setDisable(true);
            takeCardItem.setDisable(true);
            passMoveButton.setDisable(true);
            passMoveItem.setDisable(true);

            messagesLabel.setText("Please, connect to game server.");
        }

        if (Game.getOpponentCards().isEmpty()) {
            opponentCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }
        if (Game.getMyCards().isEmpty()) {
            myCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }
    }

    @FXML
    protected void onTakeCardClick() {
/*        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        Engine.getMyCards().add(new Card(value, suit));*/
        Client.takeCard();
        updateState();
    }

    @FXML
    protected void onPassMoveClick() {
/*        CardSuit suit = suits[random.nextInt(4)];
        int value = random.nextInt(10) + 2;
        if (value == 5) {
            value++;
        }
        Engine.getOpponentCards().add(new Card(value, suit));*/
        Client.passMove();
        updateState();
    }

    @FXML
    protected void onExitApplication() {
        Client.disconnect();
        System.exit(0);
    }

    @FXML
    protected void onAboutAction() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Information about this program");
        alert.setContentText("This application was made by Zaykov Konstantin\nNSTU\n2025");
        alert.showAndWait();
    }

    @FXML
    protected void onServerConnect() {
        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Label label = new Label("Server address: ");
        TextField serverAddress = new TextField(Client.getServerAddress());
        CheckBox masterCheck = new CheckBox("Start game and wait for connection");
        TextField firstNameText = new TextField(Client.getFirstName());
        TextField lastNameText = new TextField(Client.getLastName());
        HBox hBox2 = new HBox(new Label("First name: "), firstNameText, new Label("    Last name: "), lastNameText);
        hBox2.setAlignment(Pos.CENTER_LEFT);
        HBox hBox1 = new HBox(label, serverAddress);
        hBox1.setAlignment(Pos.CENTER_LEFT);
        VBox vbox = new VBox(hBox1, new Label(" "), hBox2, masterCheck);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, cancelButtonType);

        Button lookupButton = (Button) dialog.getDialogPane().lookupButton(connectButtonType);
        lookupButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validateConnection(serverAddress.getText(), firstNameText.getText(), lastNameText.getText())) {
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            Client.connect(serverAddress.getText(), firstNameText.getText(), lastNameText.getText());
            Game.initGame();
            updateState();
        }
    }

    private boolean validateConnection(String serverAddressText, String firstNameText, String lastNameText) {
        if (serverAddressText.isBlank()) {
            return false;
        }
        if ((firstNameText == null || firstNameText.isBlank()) && (lastNameText == null || lastNameText.isBlank())) {
            return false;
        }
        // OK, return true
        return true;
    }

    @FXML
    protected void onJoinGame() {
        List<GameListItemDto> gameList = Client.getGameList();
        Map<String, String> gameMap = new HashMap<>(gameList.size());
        for (var item : gameList) {
            gameMap.put(item.gameCaption(), item.gameId());
        }

        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);

        ObservableList<String> games = FXCollections.observableList(gameList.stream().map(GameListItemDto::gameCaption).toList());
        ListView<String> gameListView = new ListView<String>(games);
        gameListView.setPrefSize(250, 150);

        final String[] selectedGame = new String[1];

        MultipleSelectionModel<String> gamesSelectionModel = gameListView.getSelectionModel();
        gamesSelectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> selectedGame[0] = newValue);

        VBox vbox = new VBox(new Label("Select the game to connect:"), gameListView);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

        Button lookupButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        lookupButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (selectedGame[0] == null) {
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            if (selectedGame[0] != null && gameMap.get(selectedGame[0]) != null) {
                Client.joinGame(gameMap.get(selectedGame[0]));
                updateState();
            }
        }
    }

    @FXML
    protected void onStopGame() {
        Client.stopGame();
        Game.initGame();
        updateState();
    }
}