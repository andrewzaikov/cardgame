package ru.labs.game.model;

import ru.labs.game.rest.SuitDto;

public enum CardSuit {
    SPADES,
    DIAMOND,
    CLUBS,
    HEARTS;

    public static CardSuit convert(SuitDto suitDto) {
        return switch (suitDto) {
            case CLUB -> CLUBS;
            case DIAMOND -> DIAMOND;
            case SPADE -> SPADES;
            case HEART -> HEARTS;
        };
    }
}
