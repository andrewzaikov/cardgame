package ru.labs.game.service;

import ru.labs.game.model.Card;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private static GameStatus status = GameStatus.WAIT_OPPONENT_CONNECTION;
    private static String lastMessage = null;
    private static List<Card> myCards = new ArrayList<>();
    private static List<Card> enemyCards = new ArrayList<>();

    public static GameStatus getStatus() {
        return status;
    }

    public static void setStatus(GameStatus status) {
        Engine.status = status;
    }

    public static String getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(String lastMessage) {
        Engine.lastMessage = lastMessage;
    }

    public static List<Card> getMyCards() {
        return myCards;
    }

    public static void setMyCards(List<Card> myCards) {
        Engine.myCards = myCards;
    }

    public static List<Card> getEnemyCards() {
        return enemyCards;
    }

    public static void setEnemyCards(List<Card> enemyCards) {
        Engine.enemyCards = enemyCards;
    }

    public static int getScore(List<Card> cards) {
        int score = 0;
        for (var item : myCards) {
            score += item.value();
        }
        return score;
    }
}
