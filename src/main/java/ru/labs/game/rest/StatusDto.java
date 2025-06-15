package ru.labs.game.rest;

public enum StatusDto {
    PLAYER_MOVE,
    OPPONENT_MOVE,
    PLAYER_WON,
    PLAYER_LOST,
    DRAW,
    WAIT_OPPONENT_CONNECTION,
    CONNECTED,
    SESSION_CLOSED
}
