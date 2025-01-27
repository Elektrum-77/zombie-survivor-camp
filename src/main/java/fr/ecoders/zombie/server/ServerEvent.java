package fr.ecoders.zombie.server;

import fr.ecoders.zombie.GameState;
import java.time.Instant;
import java.util.List;

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

  record ConnectedPlayerList(List<ConnectedPlayerList.Player> players) implements ServerEvent {
    record Player(
      String username,
      boolean isReady) {}
  }

  record GameStateWrapper(GameState state) implements ServerEvent {}
}
