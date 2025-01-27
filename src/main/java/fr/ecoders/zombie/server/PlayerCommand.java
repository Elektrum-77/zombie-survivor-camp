package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Action;

public sealed interface PlayerCommand {
  enum LobbyCommand implements PlayerCommand {
    READY,
    UNREADY
  }

  record ChatMessage(String text) implements PlayerCommand {}

  record ActionWrapper(Action action) implements PlayerCommand {}
}
