package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public sealed interface Action {

  static private <E> List<E> listRemove(List<E> l, int i) {
    l = new ArrayList<>(l);
    l.remove(i);
    return l;
  }

  static private <E> List<E> listAdd(List<E> l, E e) {
    l = new ArrayList<>(l);
    l.add(e);
    return l;
  }

  LocalGameState play(LocalGameState state);

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

  static private Card.Buildable handBuildable(ArrayList<Card> hand, int index) {
    var card = handCard(hand, index);
    if (!(card instanceof Card.Buildable buildable)) {
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

  static private void validateCost(ResourceBank production, ResourceBank cost) {
    if (production.containsAll(cost)) {
      return;
    }
    var missing = production.removeAll(cost)
      .filterNegative()
      .multiply(-1);
    throw new IllegalArgumentException("Production is missing " + missing);
  }

  record AddZombie(
    String username,
    int index) implements Action {
    public LocalGameState play(LocalGameState state) {
      Objects.requireNonNull(state);
      var camps = new HashMap<>(state.camps());
      var hand = new ArrayList<>(state.hand());
      var zombie = handZombie(hand, index);
      var targetCamp = camps.get(username);
      if (targetCamp == null) {
        throw new IllegalArgumentException("User " + username + " not found");
      }
      targetCamp = targetCamp.withZombies(Action.listAdd(targetCamp.zombies(), zombie));
      camps.put(username, targetCamp);
      return new LocalGameState(camps, List.of(), zombie, hand);
    }
  }

  record CancelSearch(int index) implements Action {
    public LocalGameState play(LocalGameState state) {
      Objects.requireNonNull(state);
      var camp = state.camp();
      var cost = camp.searches()
        .get(index)
        .search();
      validateCost(camp.production(), cost);
      camp = camp.withSearches(listRemove(camp.searches(), index));
      return state.withCamp(camp);
    }
  }

  record DestroyBuilding(int index) implements Action {
    public LocalGameState play(LocalGameState state) {
      Objects.requireNonNull(state);
      var camp = state.camp();
      var cost = camp.buildings()
        .get(index)
        .production();
      validateCost(camp.production(), cost);
      camp = camp.withBuildings(listRemove(camp.buildings(), index));
      return state.withCamp(camp);
    }
  }

  record Construct(int index) implements Action {
    public LocalGameState play(LocalGameState state) {
      Objects.requireNonNull(state);
      var hand = new ArrayList<>(state.hand());
      var camps = new HashMap<>(state.camps());
      var camp = state.camp();
      var buildable = handBuildable(hand, index);
      validateCost(camp.production(), buildable.cost());
      camp = camp.withBuildings(listAdd(camp.buildings(), buildable));
      camps.put(state.currentPlayer(), camp);
      return new LocalGameState(camps, hand, state.discards(), state.currentPlayer());
    }
  }

  record Search(int index) implements Action {
    public LocalGameState play(LocalGameState state) {
      var camps = new HashMap<>(state.camps());
      var camp = state.camp();
      var hand = new ArrayList<>(state.hand());
      var searchable = handSearchable(hand, index);
      validateCost(camp.production(), camp.searchCost());
      camp = camp.withSearches(listAdd(camp.searches(), searchable));
      camps.put(state.currentPlayer(), camp);
      return new LocalGameState(camps, hand, state.discards(), state.currentPlayer());
    }
  }
}
