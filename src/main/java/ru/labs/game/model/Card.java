package ru.labs.game.model;

import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;

import static ru.labs.game.model.CardSuit.*;

public record Card(int value, CardSuit suit)  {
    private final static Map<Pair<CardSuit, Integer>, String> CARDS = new HashMap<>(52);

    static {
        CARDS.put(Pair.with(HEARTS,  2),  "/jh.jpg");
        CARDS.put(Pair.with(DIAMOND, 2),  "/jb.jpg");
        CARDS.put(Pair.with(SPADES,  2),  "/jp.jpg");
        CARDS.put(Pair.with(CLUBS,   2),  "/jk.jpg");

        CARDS.put(Pair.with(HEARTS,  3),  "/qh.jpg");
        CARDS.put(Pair.with(DIAMOND, 3),  "/qb.jpg");
        CARDS.put(Pair.with(SPADES,  3),  "/qp.jpg");
        CARDS.put(Pair.with(CLUBS,   3),  "/qk.jpg");

        CARDS.put(Pair.with(HEARTS,  4),  "/kh.jpg");
        CARDS.put(Pair.with(DIAMOND, 4),  "/kb.jpg");
        CARDS.put(Pair.with(SPADES,  4),  "/kp.jpg");
        CARDS.put(Pair.with(CLUBS,   4),  "/kk.jpg");

        CARDS.put(Pair.with(HEARTS,  6),  "/6h.jpg");
        CARDS.put(Pair.with(DIAMOND, 6),  "/6b.jpg");
        CARDS.put(Pair.with(SPADES,  6),  "/6p.jpg");
        CARDS.put(Pair.with(CLUBS,   6),  "/6k.jpg");

        CARDS.put(Pair.with(HEARTS,  7),  "/7h.jpg");
        CARDS.put(Pair.with(DIAMOND, 7),  "/7b.jpg");
        CARDS.put(Pair.with(SPADES,  7),  "/7p.jpg");
        CARDS.put(Pair.with(CLUBS,   7),  "/7k.jpg");

        CARDS.put(Pair.with(HEARTS,  8),  "/8h.jpg");
        CARDS.put(Pair.with(DIAMOND, 8),  "/8b.jpg");
        CARDS.put(Pair.with(SPADES,  8),  "/8p.jpg");
        CARDS.put(Pair.with(CLUBS,   8),  "/8k.jpg");

        CARDS.put(Pair.with(HEARTS,  9),  "/9h.jpg");
        CARDS.put(Pair.with(DIAMOND, 9),  "/9b.jpg");
        CARDS.put(Pair.with(SPADES,  9),  "/9p.jpg");
        CARDS.put(Pair.with(CLUBS,   9),  "/9k.jpg");

        CARDS.put(Pair.with(HEARTS,  10), "/10h.jpg");
        CARDS.put(Pair.with(DIAMOND, 10), "/10b.jpg");
        CARDS.put(Pair.with(SPADES,  10), "/10p.jpg");
        CARDS.put(Pair.with(CLUBS,   10), "/10k.jpg");

        CARDS.put(Pair.with(HEARTS,  11), "/ah.jpg");
        CARDS.put(Pair.with(DIAMOND, 11), "/ab.jpg");
        CARDS.put(Pair.with(SPADES,  11), "/ap.jpg");
        CARDS.put(Pair.with(CLUBS,   11), "/ak.jpg");
    }

    public static String getResourceLocation(CardSuit suit, Integer value) {
        return CARDS.get(Pair.with(suit, value));
    }
}
