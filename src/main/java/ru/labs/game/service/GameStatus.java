package ru.labs.game.service;

public enum GameStatus {
    /**
     * Connected to Server.
     * Second player not connected
     */
    WAIT_OPPONENT_CONNECTION,
    /**
     * Waiting when opponent makes his move
     */
    OPPONENTS_TURN,
    /**
     * Player should make his move
     */
    PLAYER_TURN,
    /**
     * Fix status: player has won
     */
    PLAYER_WON,
    /**
     * Fix status: opponent has won
     */
    PLAYER_LOST,
    /**
     * Fix status: draw
     */
    DRAW
}
