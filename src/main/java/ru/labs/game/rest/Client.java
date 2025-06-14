package ru.labs.game.rest;

import ru.labs.game.service.Engine;

import java.util.UUID;

import static ru.labs.game.service.GameStatus.PLAYER_TURN;

public class Client {
    private static String token;
    private static String serverAddress;
    private static Boolean requestPending = false;

    public static boolean isConnected() {
        return token != null;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Client.token = token;
    }

    public static void connect(String serverAddress) {
        //TODO: register client connection, obtain a token
        if (serverAddress == null) {
            throw new RuntimeException("Server address is not set!");
        }
        synchronized (requestPending) {
            if (!requestPending) {
                requestPending = true;

                Client.serverAddress = serverAddress;
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
        synchronized (requestPending) {
            if (!requestPending) {
                Client.serverAddress = null;
                Client.token = null;
            }
        }
    }
}
