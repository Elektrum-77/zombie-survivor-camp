package fr.ecoders.zombie.server;

import fr.ecoders.lobby.Lobby;
import fr.ecoders.zombie.state.LocalGameState;
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

  record ConnectedPlayers(List<Lobby.Player> players) implements ServerEvent {}

  record TurnStart(LocalGameState state) implements ServerEvent {}
  record TurnEnd(LocalGameState state) implements ServerEvent {}
  record TurnUpdate(LocalGameState state) implements ServerEvent {}
}
