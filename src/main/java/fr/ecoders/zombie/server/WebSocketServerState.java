package fr.ecoders.zombie.server;

public sealed interface WebSocketServerState permits InGame, InLobby {}
