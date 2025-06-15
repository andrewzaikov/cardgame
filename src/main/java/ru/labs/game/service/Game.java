package ru.labs.game.service;

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
public class Game {
    private static GameStatus status;
    private static String lastMessage;
    private static final List<Card> myCards = new ArrayList<>();
    private static final List<Card> opponentCards = new ArrayList<>();

    public static void initGame() {
        status = Client.isConnected() ? GameStatus.CONNECTED : null;
        myCards.clear();
        opponentCards.clear();
        lastMessage = null;
    }

    public static void updateState(GameInfoDto gameInfoDto) {
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

    public static GameStatus getStatus() {
        return status;
    }

    public static void setStatus(GameStatus status) {
        Game.status = status;
    }

    public static String getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(String lastMessage) {
        Game.lastMessage = lastMessage;
    }

    public static List<Card> getMyCards() {
        return myCards;
    }

    public static List<Card> getOpponentCards() {
        return opponentCards;
    }

    public static int getScore(List<Card> cards) {
        return cards.stream().mapToInt(Card::value).sum();
    }
}
