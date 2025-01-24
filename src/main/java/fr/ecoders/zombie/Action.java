package fr.ecoders.zombie;

import java.util.Objects;

public sealed interface Action {
  void play(Game game);

  final class Build implements Action {
    private final int index;
    private Build(int index) {
      this.index = index;
    }

    public Build with(GameState gameState, int index) {
      Objects.requireNonNull(gameState);
      var hand = gameState.hand();
      Objects.checkIndex(index, gameState.hand().size());
      if (!(hand.get(index) instanceof Card.Building building)) {
        throw new IllegalArgumentException("Selected card is not a building");
      }
      return new Build(index);
    };

    @Override
    public void play(Game game) {

    }
  }
}
