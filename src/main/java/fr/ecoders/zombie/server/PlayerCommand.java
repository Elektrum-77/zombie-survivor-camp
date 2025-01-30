package fr.ecoders.zombie.server;

public sealed interface PlayerCommand {
  enum LobbyCommand implements PlayerCommand {
    READY,
    UNREADY
  }

  record ChatMessage(String text) implements PlayerCommand {}

  sealed interface Action extends PlayerCommand {
    record DestroyBuilding(int index) implements Action {}
    record CancelSearch(int index) implements Action {}
    record Construct(int index) implements Action {}
    record Search(int index) implements Action {}
  }
}
