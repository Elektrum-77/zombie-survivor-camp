package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.state.GameState;
import fr.ecoders.zombie.state.ResourceBank;
import fr.ecoders.zombie.state.UpgradableBuilding;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public sealed interface Action {

  static private <E> List<E> listAdd(List<E> l, E e) {
    l = new ArrayList<>(l);
    l.add(e);
    return l;
  }

  static private void validateNotMissing(ResourceBank production, ResourceBank subtracting) {
    var missing = production.missingAll(subtracting);
    if (!missing.isEmpty()) {
      throw new IllegalArgumentException("Production is missing " + missing);
    }
  }

  static private Card handCard(ArrayList<Card> hand, int index) {
    Objects.checkIndex(index, hand.size());
    return hand.remove(index);
  }

  static private Card.Zombie handZombie(ArrayList<Card> hand, int index) {
    var card = handCard(hand, index);
    if (!(card instanceof Card.Zombie zombie)) {
      throw new IllegalArgumentException("Card " + card + " is not a zombie");
    }
    return zombie;
  }

  static private Building handBuilding(ArrayList<Card> hand, int index) {
    var card = handCard(hand, index);
    if (!(card instanceof Building buildable)) {
      throw new IllegalArgumentException("Card " + card + " is not buildable");
    }
    return buildable;
  }

  static private Card.Searchable handSearchable(ArrayList<Card> hand, int index) {
    var card = handCard(hand, index);
    if (!(card instanceof Card.Searchable searchable)) {
      throw new IllegalArgumentException("Card " + card + " is not searchable");
    }
    return searchable;
  }

  static private fr.ecoders.zombie.card.Upgrade handUpgrade(ArrayList<Card> hand, int index) {
    var card = handCard(hand, index);
    if (!(card instanceof fr.ecoders.zombie.card.Upgrade upgrade)) {
      throw new IllegalArgumentException("Card " + card + " is not Upgrade");
    }
    return upgrade;
  }

  GameState play(GameState state, String currentUsername);

  record UpgradeBuilding(
    int upgradeIndex,
    int buildingIndex) implements Action {
    @Override
    public GameState play(GameState state, String currentUsername) {
      var player = state.player(currentUsername);
      var camp = player.camp();
      var hand = new ArrayList<>(player.hand());
      var upgrade = handUpgrade(hand, upgradeIndex);
      validateNotMissing(camp.production(), upgrade.cost());
      var buildings = new ArrayList<>(camp.buildings());
      var building = buildings.get(buildingIndex);

      building = building.withUpgrades(listAdd(building.upgrades(), upgrade));
      buildings.set(buildingIndex, building);
      camp = camp.withBuildings(buildings);
      player = player.withHand(hand)
        .withCamp(camp);
      return state.withPlayer(currentUsername, player);
    }
  }

  record SendZombie(
    String username,
    int index) implements Action {

    @Override
    public GameState play(GameState state, String currentUsername) {
      var currentPlayer = state.player(currentUsername);
      var hand = new ArrayList<>(currentPlayer.hand());
      Objects.checkIndex(index, hand.size());
      var zombie = handZombie(hand, index);
      state = state.withPlayer(currentUsername, currentPlayer.withHand(hand));

      var target = state.player(username);
      var camp = target.camp();
      return state.withPlayer(username, target.withCamp(camp.withZombies(listAdd(camp.zombies(), zombie))));
    }
  }

  record CancelSearch(int index) implements Action {

    @Override
    public GameState play(GameState state, String currentUsername) {
      var player = state.player(currentUsername);
      var camp = player.camp();
      var searches = new ArrayList<>(camp.searches());
      var search = searches.remove(index);
      validateNotMissing(camp.production(), search.search());
      return state.withPlayer(currentUsername, player.withCamp(camp.withSearches(searches)));
    }
  }

  record DestroyBuilding(int index) implements Action {
    @Override
    public GameState play(GameState state, String currentUsername) {
      var player = state.player(currentUsername);
      var camp = player.camp();
      var buildings = new ArrayList<>(camp.buildings());
      var building = buildings.remove(index);
      validateNotMissing(camp.production(), building.production());
      return state.withPlayer(currentUsername, player.withCamp(camp.withBuildings(buildings)));
    }
  }

  record Construct(int index) implements Action {
    @Override
    public GameState play(GameState state, String currentUsername) {
      var player = state.player(currentUsername);
      var camp = player.camp();
      var hand = new ArrayList<>(player.hand());
      var building = handBuilding(hand, index);
      validateNotMissing(camp.production(), building.cost());
      camp = camp.withBuildings(listAdd(camp.buildings(), UpgradableBuilding.ofBuilding(building)));
      player = player.withHand(hand)
        .withCamp(camp);
      return state.withPlayer(currentUsername, player);
    }
  }

  record Search(int index) implements Action {

    @Override
    public GameState play(GameState state, String currentUsername) {
      var player = state.player(currentUsername);
      var camp = player.camp();
      var hand = new ArrayList<>(player.hand());
      var search = handSearchable(hand, index);
      validateNotMissing(camp.production(), camp.searchCost());
      camp = camp.withSearches(listAdd(camp.searches(), search));
      player = player.withHand(hand)
        .withCamp(camp);
      return state.withPlayer(currentUsername, player);
    }
  }
}
