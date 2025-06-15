package ru.labs.game.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Singleton (static). Connection to server, join a game, obtain a status etc.
 */
@Service
public class Client {
    private String token;
    private String serverAddress = "http://localhost:8080/";
    private Boolean requestPending = false;
    private String firstName;
    private String lastName;
    private String gameId;

    private final RestTemplate restTemplate;

    @Autowired
    public Client(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isConnected() {
        return token != null;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public List<GameListItemDto> getGameList() {
        //todo: stub - remove it
        return List.of(new GameListItemDto("First game", UUID.randomUUID().toString()),
                new GameListItemDto("Second game", UUID.randomUUID().toString()),
                new GameListItemDto("Third game", UUID.randomUUID().toString()));
    }

    public void joinGame(String gameId) {
        this.gameId = gameId;
    }

    public GameInfoDto getInfo() {
        //todo: stub - remove it
        return new GameInfoDto(
                List.of(new CardDto(6, SuitDto.SPADE), new CardDto(4, SuitDto.CLUB)),
                List.of(new CardDto(11, SuitDto.DIAMOND)),
                StatusDto.PLAYER_MOVE);
    }

    public void takeCard() {

    }

    public void passMove() {

    }

    public void stopGame() {

    }

    public void startGame() {

    }

    public void connect(String serverAddress, String firstName, String lastName) {
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

                this.serverAddress = serverAddress;
                this.firstName = firstName;
                this.lastName = lastName;

                //todo: get it from server
                this.token = UUID.randomUUID().toString();
                requestPending = false;
            }
        }
    }

    public void disconnect() {
        if(!isConnected()) {
            return;
        }
        synchronized (Client.class) {
            if (!requestPending) {
                this.token = null;
            }
        }
    }
}
