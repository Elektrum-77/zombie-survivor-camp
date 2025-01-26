package fr.ecoders.zombie;

import static fr.ecoders.zombie.Card.CARDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class Game {
  private final HashMap<String, Camp> camps;
  private final HashMap<String, PlayerHandler> activeHandlers;
  private final Stack stack;

  private Game(Map<String, Camp> camps, Map<String, PlayerHandler> activeHandlers, List<Card> cards) {
    this.camps = new HashMap<>(Map.copyOf(camps));
    this.stack = new Stack(List.copyOf(cards));
    this.activeHandlers = new HashMap<>(Map.copyOf(activeHandlers));
  }

  static void start(Map<String, PlayerHandler> handlers) throws InterruptedException {
    var camps = handlers.keySet()
      .stream()
      .collect(Collectors.toUnmodifiableMap(
        Function.identity(),
        n -> new Camp(6, List.of(), List.of())));
    var game = new Game(camps, Map.copyOf(handlers), CARDS);
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      while (!game.isFinished()) {

        // ask all active players to choose an action
        var futures = game.activeHandlers()
          .entrySet()
          .stream()
          .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            e -> {
              var name = e.getKey();
              var handler = e.getValue();
              return AskAction.asking(handler, game.gameState(name), executor);
            }));

        // apply all actions
        for (var e : futures.entrySet()) {
          var username = e.getKey();
          try {
            var asking = e.getValue();
            var answer = asking.answer();
            var gameState = asking.gameState();
            var action = answer.get();
            action.play(game, gameState.hand(), username);
          } catch (ExecutionException ex) {
            game.activeHandlers.remove(username);
            throw new AssertionError("An error occurred while waiting for a player's action", ex);
          }
        }
      }
    }
  }

  private GameState gameState(String player) {
    return new GameState(camps, stack.draw(3), player);
  }

  public void updateCamp(String player, UnaryOperator<Camp> updater) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(updater);
    if (!camps.containsKey(player)) {
      throw new IllegalArgumentException("Player " + player + " does not exist");
    }
    var camp = camps.get(player);
    camps.replace(player, updater.apply(camp));
  }

  public void discardAll(List<Card> cards) {
    List.copyOf(cards);
    stack.discardAll(cards);
  }

  private record AskAction(
    GameState gameState,
    Future<Action> answer) {
    private static AskAction asking(PlayerHandler handler, GameState gameState, ExecutorService executor) {
      return new AskAction(gameState, executor.submit(() -> handler.askAction(gameState)));
    }
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
