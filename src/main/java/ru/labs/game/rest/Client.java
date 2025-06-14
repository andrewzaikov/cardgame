package ru.labs.game.rest;

import ru.labs.game.service.Engine;

import java.util.List;
import java.util.UUID;

import static ru.labs.game.service.GameStatus.PLAYER_TURN;

/**
 * Singleton (static). Connection to server, join a game, obtain a status etc.
 */
public class Client {
    private static String token;
    private static String serverAddress = "http://127.0.0.1/";
    private static Boolean requestPending = false;
    private static String firstName;
    private static String lastName;
    private static String gameId;

    public static boolean isConnected() {
        return token != null;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void setServerAddress(String serverAddress) {
        Client.serverAddress = serverAddress;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Client.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Client.lastName = lastName;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static List<GameListItemDto> getGameList() {
        return List.of(new GameListItemDto("First game", UUID.randomUUID().toString()),
                new GameListItemDto("Second game", UUID.randomUUID().toString()),
                new GameListItemDto("Third game", UUID.randomUUID().toString()));
    }

    public static void joinGame(String gameId) {
        Client.gameId = gameId;
    }

    public static GameInfoDto getInfo() {
        return new GameInfoDto(
                List.of(new CardDto(6, SuitDto.SPADE), new CardDto(4, SuitDto.CLUB)),
                List.of(new CardDto(11, SuitDto.DIAMOND)),
                StatusDto.PLAYER_MOVE);
    }

    public static void takeCard() {

    }

    public static void passMove() {

    }

    public static void stopGame() {

    }

    public static StatusDto startGame() {
        return StatusDto.WAIT_CONNECTION;
    }

    public static void connect(String serverAddress, String firstName, String lastName) {
        //TODO: register client connection, obtain a token
        if (isBlank(serverAddress)) {
            throw new RuntimeException("Server address is not set!");
        }
        if (isBlank(firstName) && isBlank(lastName)) {
            throw new RuntimeException("First name or last name must be set!");
        }

        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;

                Client.serverAddress = serverAddress;
                Client.firstName = firstName;
                Client.lastName = lastName;

                // get from server
                Client.token = UUID.randomUUID().toString();

                Engine.setStatus(PLAYER_TURN);
                Engine.setLastMessage("Connected to server!");

                requestPending = false;
            }
        }
    }

    public static void disconnect() {
        if(!isConnected()) {
            return;
        }
        synchronized (Client.class) {
            if (!requestPending) {
                Client.token = null;
            }
        }
    }
}
