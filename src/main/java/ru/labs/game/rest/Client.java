package ru.labs.game.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static ru.labs.game.rest.StatusDto.SESSION_CLOSED;

/**
 * Spring bean. Connection to server, join a game, obtain a status etc.
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
        String exceptionMessage = null;
        synchronized (Client.class) {
            if(!requestPending) {
                requestPending = true;
                try {
                    List<Map<String, String>> reply = restTemplate.getForObject(serverAddress+"/gameList?session="+token, List.class);
                    System.out.println("gameList:");
                    System.out.println(reply);
                    if (reply != null) {
                        for (Map<String, String> item : reply) {
                            gameList.add(new GameListItemDto(item.get("gameCaption"), item.get("gameId")));
                        }
                    }
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return gameList;
    }

    public String joinGame(String gameId) {
        String exceptionMessage = null;
        synchronized (Client.class) {
            if(!requestPending) {
                requestPending = true;
                try {
                    this.gameId = gameId;
                    restTemplate.postForObject(serverAddress+"/join?session="+token+"&id="+gameId, null, Object.class);
                    System.out.println("joinGame: post id="+gameId);
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
    }

    public GameInfoDto getInfo() {
        GameInfoDto gameInfoDto = null;
        String exceptionMessage = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                try {
                    gameInfoDto = restTemplate.getForObject(serverAddress + "/getInfo?session=" + token, GameInfoDto.class);
                    System.out.println("gameInfo:");
                    System.out.println(gameInfoDto);
                } catch (HttpClientErrorException e) {
                    gameInfoDto = createExceptionInfo(processHttpClientException(e));
                } catch (RestClientException e) {
                    gameInfoDto = createExceptionInfo(processRestException(e));
                }
                requestPending = false;
            }
        }
        return gameInfoDto;
    }

    private GameInfoDto createExceptionInfo(String exceptionMessage) {
        return new GameInfoDto(Collections.emptyList(), Collections.emptyList(), SESSION_CLOSED, exceptionMessage);
    }

    private String processRestException(RestClientException e) {
        System.out.println(e);
        token = null;
        gameId = null;
        return e.getMessage();
    }

    private String processHttpClientException(HttpClientErrorException e) {
        if (e.getStatusCode().is4xxClientError()) {
            return processRestException(e);
        }
        return e.getMessage();
    }

    public String takeCard() {
        String exceptionMessage = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                try {
                    restTemplate.postForObject(serverAddress+"/takeCard?session="+token, null, Object.class);
                    System.out.println("takeCard: post");
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
    }

    public String passMove() {
        String exceptionMessage = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                try {
                    restTemplate.postForObject(serverAddress+"/passMove?session="+token, null, Object.class);
                    System.out.println("passMove: post");
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
    }

    public String stopGame() {
        String exceptionMessage = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                try {
                    restTemplate.postForObject(serverAddress+"/stopGame?session="+token, null, Object.class);
                    System.out.println("stopGame: post");
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
    }

    public String startGame() {
        String exceptionMessage = null;
        synchronized (Client.class) {
            if (!requestPending) {
                requestPending = true;
                try {
                    String reply = restTemplate.postForObject(serverAddress+"/startGame?session="+token, null, String.class);
                    System.out.println("startGame: post id="+reply);
                    gameId = reply;
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
    }

    public String connect(String serverAddress, String firstName, String lastName) {
        //TODO: register client connection, obtain a token
        if (isBlank(serverAddress)) {
            return "Server address is not set!";
        }
        if (isBlank(firstName) && isBlank(lastName)) {
            return "First name or last name must be set!";
        }

        String exceptionMessage = null;
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

                try {
                    token = restTemplate.postForObject(
                            serverAddress+"/register?firstName="+firstName+"&lastName="+lastName,
                            token,
                            String.class
                            );
                    System.out.println("session="+token);
                } catch (HttpClientErrorException e) {
                    exceptionMessage = processHttpClientException(e);
                } catch (RestClientException e) {
                    exceptionMessage = processRestException(e);
                }
                requestPending = false;
            }
        }
        return exceptionMessage;
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
