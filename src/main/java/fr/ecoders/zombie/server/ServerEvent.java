package fr.ecoders.zombie.server;

import fr.ecoders.zombie.GameState;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public sealed interface ServerEvent {
  record ChatMessage(
    String username,
    String text,
    Instant timestamp) implements ServerEvent {}

  record LobbyEvent(
    String player,
    State state) implements ServerEvent {
    enum State {
      CONNECT,
      DISCONNECT,
      READY,
      UNREADY
    }
  }

  record ConnectedPlayers(Map<String, Boolean> players) implements ServerEvent {}

  record GameStateWrapper(GameState state) implements ServerEvent {}
}
