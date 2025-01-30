package fr.ecoders.zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class PlayerTurn {
  private final Consumer<Game> consumer;

  private PlayerTurn(Consumer<Game> consumer) {
    this.consumer = consumer;
  }

  private static Camp cancelSearch(Camp camp, int index) {
    var searches = new ArrayList<>(camp.searches());
    Objects.checkIndex(index, searches.size());
    searches.remove(index);
    return camp.withSearches(searches);
  }

  private static Camp destroyBuilding(Camp camp, int index) {
    var buildings = new ArrayList<>(camp.buildings());
    Objects.checkIndex(index, buildings.size());
    buildings.remove(index);
    return camp.withBuildings(buildings);
  }

  private static Camp construct(Camp camp, Card.Buildable buildable) {
    var buildings = new ArrayList<>(camp.buildings());
    if (buildings.size() >= camp.maxBuildCount()) {
      throw new IllegalArgumentException("too many buildings");
    }
    var production = camp.production();
    if (!production.containsAll(buildable.cost())) {
      throw new IllegalArgumentException("not enough resources to build : " + buildable);
    }
    buildings.add(buildable);
    return camp.withBuildings(buildings);
  }

  private static Camp search(Camp camp, Card.Searchable searchable) {
    var searches = new ArrayList<>(camp.searches());
    var production = camp.production();
    if (!production.containsAll(Camp.SEARCH_COST)) {
      throw new IllegalArgumentException("not enough resources (%s) for camp (%s) to search : %s".formatted(
        production, camp, searchable));
    }
    searches.add(searchable);
    return camp.withSearches(searches);
  }

  public static Builder with() {
    return new Builder();
  }

  void play(Game game) {
    consumer.accept(game);
  }

  public static final class Builder {
    private Consumer<Game> consumer = _ -> { /* nothing */ };
    private boolean isDone = false;

    private Builder() {
    }

    public LocalGameState cancelSearch(LocalGameState state, int index) {
      if (isDone) {
        throw new IllegalStateException("player turn is already done");
      }
      Objects.requireNonNull(state);
      var camp = state.camp();
      var username = state.currentPlayer();

      state = state.withCamp(PlayerTurn.cancelSearch(camp, index));
      consumer = consumer.andThen(
        game -> game.setCamp(username, PlayerTurn.cancelSearch(game.camp(username), index)));
      return state;
    }

    public LocalGameState destroyBuilding(LocalGameState state, int index) {
      if (isDone) {
        throw new IllegalStateException("player turn is already done");
      }
      Objects.requireNonNull(state);
      var camp = state.camp();
      var username = state.currentPlayer();

      state = state.withCamp(PlayerTurn.destroyBuilding(camp, index));
      consumer = consumer.andThen(
        game -> game.setCamp(username, PlayerTurn.destroyBuilding(game.camp(username), index)));
      return state;
    }

    public LocalGameState construct(LocalGameState state, int index) {
      if (isDone) {
        throw new IllegalStateException("player turn is already done");
      }
      Objects.requireNonNull(state);
      var camp = state.camp();
      var username = state.currentPlayer();
      var hand = new ArrayList<>(state.hand());
      Objects.checkIndex(index, hand.size());
      var card = hand.remove(index);
      if (!(card instanceof Card.Buildable buildable)) {
        throw new IllegalArgumentException("card (" + card + ") is not buildable");
      }

      state = state.withCamp(PlayerTurn.construct(camp, buildable))
        .withHand(List.of());
      consumer = consumer.andThen(game -> {
        game.setCamp(username, PlayerTurn.construct(game.camp(username), buildable));
        game.discardAll(hand);
      });
      isDone = true;
      return state;
    }

    public LocalGameState search(LocalGameState state, int index) {
      if (isDone) {
        throw new IllegalStateException("player turn is already done");
      }
      Objects.requireNonNull(state);
      var camp = state.camp();
      var username = state.currentPlayer();
      var hand = new ArrayList<>(state.hand());
      Objects.checkIndex(index, hand.size());
      var card = hand.remove(index);
      if (!(card instanceof Card.Searchable searchable)) {
        throw new IllegalArgumentException("card (" + card + ") is not searchable");
      }

      state = state.withCamp(PlayerTurn.search(camp, searchable))
        .withHand(List.of());
      consumer = consumer.andThen(game -> {
        game.setCamp(username, PlayerTurn.search(game.camp(username), searchable));
        game.discardAll(hand);
      });
      isDone = true;
      return state;
    }

    public PlayerTurn build() {
      if (!isDone) {
        throw new IllegalStateException("player turn is not done");
      }
      return new PlayerTurn(consumer);
    }

    public boolean isDone() {
      return isDone;
    }
  }
}
