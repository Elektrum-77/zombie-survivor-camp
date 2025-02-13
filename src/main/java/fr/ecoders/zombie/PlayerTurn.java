package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.card.Card.Buildable;
import fr.ecoders.zombie.card.Card.Searchable;
import fr.ecoders.zombie.card.Card.Zombie;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface PlayerTurn {
  static Builder of(LocalGameState state) {
    Objects.requireNonNull(state);
    return new Builder(state);
  }

  void play(Game game, String currentUser, List<Card> hand);

  public static final class Builder {
    private final ArrayList<Action> actions = new ArrayList<>();
    private LocalGameState state;
    private boolean isDone = false;

    private Builder(LocalGameState state) {
      this.state = state;
    }

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

    public LocalGameState state() {
      return state;
    }

    private Card handCard(List<Card> hand, int index) {
      Objects.checkIndex(index, hand.size());
      isDone = true;
      return hand.get(index);
    }

    private Zombie handZombie(List<Card> hand, int index) {
      var card = handCard(hand, index);
      if (!(card instanceof Zombie zombie)) {
        throw new IllegalArgumentException("Card " + card + " is not a zombie");
      }
      return zombie;
    }

    private Buildable handBuildable(List<Card> hand, int index) {
      var card = handCard(hand, index);
      if (!(card instanceof Buildable buildable)) {
        throw new IllegalArgumentException("Card " + card + " is not buildable");
      }
      return buildable;
    }

    private Searchable handSearchable(List<Card> hand, int index) {
      var card = handCard(hand, index);
      if (!(card instanceof Searchable searchable)) {
        throw new IllegalArgumentException("Card " + card + " is not searchable");
      }
      return searchable;
    }

    public LocalGameState add(Action action) {
      Objects.requireNonNull(action);
      if (isDone) {
        throw new IllegalStateException("Turn is already done");
      }
      var camp = state.camp();
      state = switch (action) {
        case Action.AddZombie(String targetUser, int handIndex) -> {
          var targetCamp = state.userCamp(targetUser);
          var zombie = handZombie(state.hand(), handIndex);
          yield state.withUserCamp(targetUser, targetCamp.withZombies(listAdd(targetCamp.zombies(), zombie)));
        }
        case Action.CancelSearch(int searchIndex) ->
          state.withCamp(camp.withSearches(listRemove(camp.searches(), searchIndex)));
        case Action.Construct(int handIndex) -> {
          var building = handBuildable(state.hand(), handIndex);
          yield state.withCamp(camp.withBuildings(listAdd(camp.buildings(), building)));
        }
        case Action.DestroyBuilding(int buildingIndex) ->
          state.withCamp(camp.withBuildings(listRemove(camp.buildings(), buildingIndex)));
        case Action.Search(int handIndex) -> {
          var searchable = handSearchable(state.hand(), handIndex);
          var production = camp.production();
          if (production.containsAll(camp.searchCost())) {
            var missing = production.removeAll(camp.searchCost())
              .filterNegative()
              .multiply(-1);
            throw new IllegalArgumentException("Not enough resources produced to search. Missing " + missing);
          }
          yield state.withCamp(camp.withSearches(listAdd(camp.searches(), searchable)));
        }
      };
      actions.add(action);
      return state;
    }


    public boolean isDone() {
      return isDone;
    }
  }
}
