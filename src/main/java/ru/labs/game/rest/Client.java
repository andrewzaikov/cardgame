package ru.labs.game.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        List<GameListItemDto> gameList = new LinkedList<>();
        synchronized (Client.class) {
            if(!requestPending) {
                requestPending = true;
                List<Map<String, String>> reply = restTemplate.getForObject(serverAddress+"/gameList?session="+token, List.class);
                System.out.println("gameList:");
                System.out.println(reply);
                for (Map<String, String> item: reply) {
                    gameList.add(new GameListItemDto(item.get("gameCaption"), item.get("gameId")));
                }
                requestPending = false;
            }
        }
        return gameList;
    }

    public void joinGame(String gameId) {
        synchronized (Client.class) {
            if(!requestPending) {
                requestPending = true;
                this.gameId = gameId;
                restTemplate.postForObject(serverAddress+"/join?session="+token+"&id="+gameId, null, Object.class);
                System.out.println("joinGame: post");
                requestPending = false;
            }
        }
    }

    public GameInfoDto getInfo() {
        GameInfoDto gameInfoDto = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                gameInfoDto = restTemplate.getForObject(serverAddress+"/getInfo?session="+token, GameInfoDto.class);
                System.out.println("gameInfo:");
                System.out.println(gameInfoDto);
                requestPending = false;
            }
        }
        return gameInfoDto;
    }

    public void takeCard() {
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                restTemplate.postForObject(serverAddress+"/takeCard?session="+token, null, Object.class);
                System.out.println("takeCard: post");
                requestPending = false;
            }
        }
    }

    public void passMove() {
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                restTemplate.postForObject(serverAddress+"/passMove?session="+token, null, Object.class);
                System.out.println("passMove: post");
                requestPending = false;
            }
        }
    }

    public void stopGame() {
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                restTemplate.postForObject(serverAddress+"/stopGame?session="+token, null, Object.class);
                System.out.println("stopGame: post");
                requestPending = false;
            }
        }
    }

    public void startGame() {
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                String reply = restTemplate.postForObject(serverAddress+"/startGame?session="+token, null, String.class);
                System.out.println("startGame: post="+reply);
                gameId = reply;
                requestPending = false;
            }
        }
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

                if (serverAddress.endsWith("/")) {
                    this.serverAddress = serverAddress.substring(0, serverAddress.length()-1);
                } else {
                    this.serverAddress = serverAddress;
                }
                System.out.println("serverAddress="+this.serverAddress);
                this.firstName = firstName;
                this.lastName = lastName;

                token = restTemplate.postForObject(
                        serverAddress+"/register?firstName="+firstName+"&lastName="+lastName,
                        token,
                        String.class
                        );
                System.out.println("session="+token);
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
