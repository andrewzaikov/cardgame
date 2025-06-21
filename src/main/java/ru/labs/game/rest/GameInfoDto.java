package ru.labs.game.rest;

import java.util.List;

public record GameInfoDto(List<CardDto> myCards, List<CardDto> opponentCards, StatusDto status, String exceptionMessage) {
}
