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
import java.util.function.UnaryOperator;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class Game {
  private final HashMap<String, Camp> camps;
  private final HashMap<String, Player.Handler> activeHandlers;
  private final Stack stack;

  private Game(HashMap<String, Camp> camps, HashMap<String, Player.Handler> activeHandlers, Stack stack) {
    this.camps = camps;
    this.activeHandlers = activeHandlers;
    this.stack = stack;
  }

  public static void start(Map<String, Player> players) throws InterruptedException {
    players = Map.copyOf(players);
    var camps = players.entrySet()
      .stream()
      .collect(toMap(Map.Entry::getKey, e -> e.getValue().camp, (_1, _2) -> null, HashMap::new));
    var handlers = players.entrySet()
      .stream()
      .collect(toMap(Map.Entry::getKey, e -> e.getValue().handler, (_1, _2) -> null, HashMap::new));
    var game = new Game(camps, handlers, new Stack(CARDS));


    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      while (!game.isFinished()) {
        var activeHandlers = game.activeHandlers();
        if (activeHandlers.isEmpty()) {
          return;
        }

        // ask all active players to choose an action
        var futures = game.activeHandlers()
          .entrySet()
          .stream()
          .collect(toUnmodifiableMap(
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
            throw new AssertionError("An error occurred while waiting for " + username + "'s action", ex);
          }
        }
      }
    }
  }

  private Map<String, Player.Handler> activeHandlers() {
    return Map.copyOf(activeHandlers);
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

  public record Player(
    String name,
    Camp camp,
    Handler handler) {
    public Player {
      Objects.requireNonNull(name);
      Objects.requireNonNull(camp);
      Objects.requireNonNull(handler);
    }

    @FunctionalInterface
    public interface Handler {
      Action askAction(GameState gs) throws InterruptedException;
    }
  }

  private record AskAction(
    GameState gameState,
    Future<Action> answer) {
    private static AskAction asking(Player.Handler handler, GameState gameState, ExecutorService executor) {
      return new AskAction(gameState, executor.submit(() -> handler.askAction(gameState)));
    }
  }

  private boolean isFinished() {
    return stack.isEmpty();
  }

}
