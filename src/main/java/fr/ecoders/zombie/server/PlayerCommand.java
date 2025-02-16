package fr.ecoders.zombie.server;

public sealed interface PlayerCommand {
  enum LobbyCommand implements PlayerCommand {
    READY,
    UNREADY
  }

  record ChatMessage(String text) implements PlayerCommand {}

  record ActionWrapper(fr.ecoders.zombie.Action action) implements PlayerCommand {}
}
