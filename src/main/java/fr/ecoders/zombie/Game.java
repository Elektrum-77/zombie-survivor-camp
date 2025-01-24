package fr.ecoders.zombie;

import static fr.ecoders.zombie.Card.CARDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public final class Game {
  private final HashMap<String, PlayerHandler> activeHandlers;
  private final Stack stack;

  private Game(Map<String, PlayerHandler> activeHandlers, List<Card> cards) {
    this.stack = new Stack(List.copyOf(cards));
    this.activeHandlers = new HashMap<>(activeHandlers);
  }

  static void start(Map<String, PlayerHandler> handlers) throws InterruptedException {
    var game = new Game(Map.copyOf(handlers), CARDS);
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      while (!game.isFinished()) {

        // ask all active players to choose an action
        var futures = game.activeHandlers()
          .entrySet()
          .stream()
          .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            e -> {
              var handler = e.getValue();
              return executor.submit(() -> handler.askAction(game.gameState()));
            }));

        // apply all actions
        for (var e : futures.entrySet()) {
          var username = e.getKey();
          try {
            var future = e.getValue();
            var action = future.get();
            action.play(game);
          } catch (ExecutionException ex) {
            game.activeHandlers.remove(username);
            throw new AssertionError("An error occurred while waiting for a player's action", ex);
          }
        }
      }
    }
  }

  private GameState gameState() {
    return new GameState(Map.of(), stack.draw(3));
  }

  private Map<String, PlayerHandler> activeHandlers() {
    return Map.copyOf(activeHandlers);
  }

  private boolean isFinished() {
    return stack.isEmpty();
  }

  @FunctionalInterface
  public interface PlayerHandler {
    Action askAction(GameState gs) throws InterruptedException;
  }
}
