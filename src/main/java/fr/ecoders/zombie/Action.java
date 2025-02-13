package fr.ecoders.zombie;

public sealed interface Action {
  record AddZombie(String targetUser, int handIndex) implements Action {}

  record CancelSearch(int searchIndex) implements Action {}

  record DestroyBuilding(int buildingIndex) implements Action {}

  record Construct(int handIndex) implements Action {}

  record Search(int handIndex) implements Action {}
}
