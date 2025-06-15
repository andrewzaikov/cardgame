package ru.labs.game.rest;

import java.util.List;
import java.util.UUID;

/**
 * Singleton (static). Connection to server, join a game, obtain a status etc.
 */
public class Client {
    private static String token;
    private static String serverAddress = "http://localhost:8080/";
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
        //todo: stub - remove it
        return List.of(new GameListItemDto("First game", UUID.randomUUID().toString()),
                new GameListItemDto("Second game", UUID.randomUUID().toString()),
                new GameListItemDto("Third game", UUID.randomUUID().toString()));
    }

    public static void joinGame(String gameId) {
        Client.gameId = gameId;
    }

    public static GameInfoDto getInfo() {
        //todo: stub - remove it
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

    public static void startGame() {

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

                //todo: get it from server
                Client.token = UUID.randomUUID().toString();
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
