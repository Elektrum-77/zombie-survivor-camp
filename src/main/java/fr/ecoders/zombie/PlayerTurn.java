package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlayerTurn {
  private final List<Action> actions;

  private PlayerTurn(List<Action> actions) {
    this.actions = actions;
  }

  static Builder of(LocalGameState state) {
    Objects.requireNonNull(state);
    return new Builder(state);
  }

  public static Builder builder(LocalGameState state) {
    Objects.requireNonNull(state);
    return new Builder(state);
  }

  public LocalGameState play(LocalGameState state) {
    Objects.requireNonNull(state);
    for (Action action : actions) {
      state = action.play(state);
    }
    return state;
  }

  public static final class Builder {
    private final ArrayList<Action> actions = new ArrayList<>();
    private LocalGameState state;
    private boolean isDone = false;

    private Builder(LocalGameState state) {
      this.state = state;
    }

    public LocalGameState state() {
      return state;
    }

    public LocalGameState add(Action action) {
      Objects.requireNonNull(action);
      if (isDone) {
        throw new IllegalStateException("Turn is already done");
      }
      state = action.play(state);
      isDone = switch (action) {
        case Action.AddZombie _, Action.Construct _, Action.Search _ -> true;
        case Action.CancelSearch _, Action.DestroyBuilding _ -> false;
      };
      actions.add(action);
      return state;
    }


    public boolean isDone() {
      return isDone;
    }

    public PlayerTurn build() {
      return new PlayerTurn(List.copyOf(actions));
    }
  }
}
