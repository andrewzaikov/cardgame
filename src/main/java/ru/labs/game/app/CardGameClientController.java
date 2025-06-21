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
import ru.labs.game.rest.Client;
import ru.labs.game.rest.GameInfoDto;
import ru.labs.game.rest.GameListItemDto;
import ru.labs.game.rest.StatusDto;
import ru.labs.game.service.Game;
import ru.labs.game.service.GameStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CardGameClientController {
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

    private Game gameEngine;

    private Client restClient;

    private static ImageView createImageViewWithCard(Card card, boolean hideCard) {
        String resource = "/suit.jpg";
        if (!hideCard) {
            resource = Card.getResourceLocation(card.suit(), card.value());
        }
        return new ImageView(new Image(resource));
    }

    public void setGameEngine(Game gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void setRestClient(Client restClient) {
        this.restClient = restClient;
    }

    public void start(Stage stage, Scene scene) {
        updateState();
    }

    private void updateState() {
        myCards.getChildren().clear();
        opponentCards.getChildren().clear();

        if (restClient.isConnected()) {
            GameInfoDto gameInfoDto = restClient.getInfo();
            if (gameInfoDto.exceptionMessage() != null) {
                showExceptionDialog(gameInfoDto.exceptionMessage());
            }
            gameEngine.updateState(gameInfoDto);

            if (gameInfoDto.status() == StatusDto.SESSION_CLOSED) {
                restClient.disconnect();

            } else {
                connectToServerItem.setDisable(true);
                stopGameItem.setDisable(false);
                joinGameItem.setDisable(gameEngine.getStatus() != GameStatus.CONNECTED);

                for (Card card : gameEngine.getMyCards()) {
                    myCards.getChildren().add(createImageViewWithCard(card, false));
                }
                boolean hideCard = (gameInfoDto.status() == StatusDto.PLAYER_MOVE || gameInfoDto.status() == StatusDto.OPPONENT_MOVE);
                for (Card card : gameEngine.getOpponentCards()) {
                    opponentCards.getChildren().add(createImageViewWithCard(card, hideCard));
                }

                opponentCardsLabel.setText("Cards of your opponent, score =" + (hideCard ? " ?" : " " + gameEngine.getScore(gameEngine.getOpponentCards())));
                myCardsLabel.setText("Your cards, score = " + gameEngine.getScore(gameEngine.getMyCards()));

                boolean buttonsDisabled = (gameEngine.getStatus() != GameStatus.PLAYER_TURN);
                takeCardButton.setDisable(buttonsDisabled);
                takeCardItem.setDisable(buttonsDisabled);
                passMoveButton.setDisable(buttonsDisabled);
                passMoveItem.setDisable(buttonsDisabled);

                switch (gameEngine.getStatus()) {
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

        if (!restClient.isConnected()) {
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

        if (gameEngine.getOpponentCards().isEmpty()) {
            opponentCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }
        if (gameEngine.getMyCards().isEmpty()) {
            myCards.getChildren().add(new ImageView(new Image("/suit.jpg")));
        }
    }

    @FXML
    protected void onTakeCardClick() {
        String exceptionMessage = restClient.takeCard();
        showExceptionDialog(exceptionMessage);
        updateState();
    }

    @FXML
    protected void onPassMoveClick() {
        String exceptionMessage = restClient.passMove();
        showExceptionDialog(exceptionMessage);
        updateState();
    }

    @FXML
    protected void onRefreshClick() {
        updateState();
    }

    @FXML
    protected void onExitApplication() {
        restClient.disconnect();
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
        TextField serverAddress = new TextField(restClient.getServerAddress());
        CheckBox masterCheck = new CheckBox("Start game and wait for connection");
        TextField firstNameText = new TextField(restClient.getFirstName());
        TextField lastNameText = new TextField(restClient.getLastName());
        HBox hBox2 = new HBox(new Label("First name: "), firstNameText, new Label("    Last name: "), lastNameText);
        hBox2.setAlignment(Pos.CENTER_LEFT);
        HBox hBox1 = new HBox(label, serverAddress);
        hBox1.setAlignment(Pos.CENTER_LEFT);
        VBox vbox = new VBox(hBox1, new Label(" "), hBox2, masterCheck);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, connectButtonType);

        Button lookupButton = (Button) dialog.getDialogPane().lookupButton(connectButtonType);
        lookupButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validateConnection(serverAddress.getText(), firstNameText.getText(), lastNameText.getText())) {
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            String exceptionMessage = restClient.connect(serverAddress.getText(), firstNameText.getText(), lastNameText.getText());
            gameEngine.initGame();
            if (masterCheck.isSelected()) {
                exceptionMessage = restClient.startGame();
            }
            showExceptionDialog(exceptionMessage);
            updateState();
        }
    }

    private void showExceptionDialog(String exceptionMessage) {
        if (exceptionMessage == null) {
            return;
        }
        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);

        VBox vbox = new VBox(new Label("ERROR:"), new Label(exceptionMessage));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

        Optional<ButtonType> result = dialog.showAndWait();
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
        List<GameListItemDto> gameList = restClient.getGameList();
        Map<String, String> gameMap = new HashMap<>(gameList.size());
        for (GameListItemDto gameListItemDto : gameList) {
            gameMap.put(gameListItemDto.gameCaption(), gameListItemDto.gameId());
        }

        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);

        ObservableList<String> games = FXCollections.observableList(gameList.stream().map(GameListItemDto::gameCaption).toList());
        ListView<String> gameListView = new ListView<>(games);
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
                String exceptionMessage = restClient.joinGame(gameMap.get(selectedGame[0]));
                showExceptionDialog(exceptionMessage);
                updateState();
            }
        }
    }

    @FXML
    protected void onStopGame() {
        String exceptionMessage = restClient.stopGame();
        showExceptionDialog(exceptionMessage);
        gameEngine.initGame();
        updateState();
    }
}