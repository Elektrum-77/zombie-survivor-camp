package fr.ecoders.zombie;

import fr.ecoders.zombie.Action.AddZombie;
import fr.ecoders.zombie.Action.CancelSearch;
import fr.ecoders.zombie.Action.Construct;
import fr.ecoders.zombie.Action.DestroyBuilding;
import fr.ecoders.zombie.Action.Search;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PlayerTurn {
  private final List<Action> actions;

  private PlayerTurn(List<Action> actions) {
    this.actions = actions;
  }

  public static Builder builder(GameState state, String currentPlayer) {
    Objects.requireNonNull(state);
    return new Builder(currentPlayer, state);
  }

  public GameState play(GameState state, String currentPlayer) {
    Objects.requireNonNull(state);
    for (Action action : actions) {
      state = action.play(state, currentPlayer);
    }
    return state;
  }

  public static final class Builder {
    private final ArrayList<Action> actions = new ArrayList<>();
    private final String currentPlayer;
    private boolean isDone = false;
    private GameState state;

    private Builder(String currentPlayer, GameState state) {
      this.currentPlayer = currentPlayer;
      this.state = state;
    }

    public LocalGameState localState() {
      var camps = state.players()
        .entrySet()
        .stream()
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().camp()));
      var player = state.player(currentPlayer);
      return new LocalGameState(camps, player.hand(), List.of(), currentPlayer);
    }

    public Builder add(Action action) {
      Objects.requireNonNull(action);
      if (isDone) {
        throw new IllegalStateException("Turn is already done");
      }
      state = action.play(state, currentPlayer);
      isDone = switch (action) {
        case AddZombie _, Construct _, Search _ -> true;
        case CancelSearch _, DestroyBuilding _ -> false;
      };
      actions.add(action);
      return this;
    }


    public boolean isDone() {
      return isDone;
    }

    public PlayerTurn build() {
      if (!isDone) {
        throw new IllegalStateException("Turn is not done");
      }
      return new PlayerTurn(List.copyOf(actions));
    }
  }
}
