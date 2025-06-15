package ru.labs.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.labs.game.model.Card;
import ru.labs.game.model.CardSuit;
import ru.labs.game.rest.Client;
import ru.labs.game.rest.GameInfoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton (static).
 * Most of game-engine functions: cards, status etc.
 */
@Service
public class Game {
    private GameStatus status;
    private String lastMessage;
    private final List<Card> myCards = new ArrayList<>();
    private final List<Card> opponentCards = new ArrayList<>();

    private final Client restClient;

    @Autowired
    public Game(Client restClient) {
        this.restClient = restClient;
    }

    public void initGame() {
        status = restClient.isConnected() ? GameStatus.CONNECTED : null;
        myCards.clear();
        opponentCards.clear();
        lastMessage = null;
    }

    public void updateState(GameInfoDto gameInfoDto) {
        myCards.clear();
        for (var item : gameInfoDto.myCards()) {
            myCards.add(new Card(item.value(), CardSuit.convert(item.suit())));
        }

        opponentCards.clear();
        for (var item : gameInfoDto.opponentCards()) {
            opponentCards.add(new Card(item.value(), CardSuit.convert(item.suit())));
        }

        status = switch (gameInfoDto.status()) {
            case PLAYER_MOVE -> GameStatus.PLAYER_TURN;
            case OPPONENT_MOVE -> GameStatus.OPPONENTS_TURN;
            case PLAYER_WON -> GameStatus.PLAYER_WON;
            case PLAYER_LOST -> GameStatus.PLAYER_LOST;
            case DRAW -> GameStatus.DRAW;
            case CONNECTED -> GameStatus.CONNECTED;
            case WAIT_OPPONENT_CONNECTION -> GameStatus.WAIT_OPPONENT_CONNECTION;
            case SESSION_CLOSED -> null;
        };
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<Card> getMyCards() {
        return myCards;
    }

    public List<Card> getOpponentCards() {
        return opponentCards;
    }

    public int getScore(List<Card> cards) {
        return cards.stream().mapToInt(Card::value).sum();
    }
}
