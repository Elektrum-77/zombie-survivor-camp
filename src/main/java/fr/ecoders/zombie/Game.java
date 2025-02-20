package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;

public final class Game {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private final LinkedHashMap<String, Camp> camps;
  private final LinkedHashMap<String, Player.Handler> activeHandlers;
  private final Stack stack;

  private Game(LinkedHashMap<String, Camp> camps, LinkedHashMap<String, Player.Handler> activeHandlers, Stack stack) {
    this.camps = camps;
    this.activeHandlers = activeHandlers;
    this.stack = stack;
  }

  public static void start(List<PlayerInfo> infos, GameOption option) throws InterruptedException {
    var cards = new ArrayList<>(option.cards());
    infos = List.copyOf(infos);
    if (option.minPlayerCount() < 1) {
      throw new IllegalArgumentException("minPlayerCount must be greater than 0");
    }
    var distinctNameCount = infos.stream()
      .map(PlayerInfo::name)
      .distinct()
      .count();
    if (distinctNameCount != infos.size()) {
      throw new IllegalArgumentException("Some players have the same name");
    }


    Collections.shuffle(cards);
    var stack = new Stack(cards);
    var camps = infos.stream()
      .collect(toMap(PlayerInfo::name, _->option.baseCamp(), (_, _) -> null, LinkedHashMap::new));
    var handlers = infos.stream()
      .collect(toMap(PlayerInfo::name, PlayerInfo::handler, (_, _) -> null, LinkedHashMap::new));
    var game = new Game(camps, handlers, stack);
    LOGGER.info("Turn evaluation order defined as " + handlers.keySet());

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      while (!game.isFinished() && !Thread.interrupted()) {
        var activeHandlers = game.activeHandlers();
        if (activeHandlers.size() < option.minPlayerCount()) {
          LOGGER.info("not enough players to continue, stopping game");
          return;
        }

        // build all players' turn
        var futures = game.activeHandlers()
          .entrySet()
          .stream()
          .map(e -> {
            var name = e.getKey();
            var handler = e.getValue();
            return AskAction.asking(handler, game.localState(name), executor);
          })
          .toList();

        // apply all turns
        for (var o : futures) {
          var localGameState = o.localGameState();
          var hand = new ArrayList<>(localGameState.hand());
          var username = localGameState.currentPlayer();
          var future = o.answer();
          try {
            var turn = future.get();
            var state = turn.play(game.localState(username));
            // TODO update game from state
          } catch (ExecutionException ex) {
            game.activeHandlers.remove(username);
            game.discardAll(hand);
            var cause = ex.getCause();
            if (!(cause instanceof InterruptedException)) {
              throw new AssertionError("An error occurred while waiting for " + username + "'s action", cause);
            }
          }
        }
      }
    }
  }

  private LocalGameState localState(String player) {
    return new LocalGameState(camps, stack.draw(3), List.of(), player);
  }

  private Map<String, Player.Handler> activeHandlers() {
    return Map.copyOf(activeHandlers);
  }

  public record PlayerInfo(
    String name,
    Player.Handler handler
  ) {
    public PlayerInfo {
      Objects.requireNonNull(name);
      Objects.requireNonNull(handler);
    }
  }

  public Camp camp(String username) {
    if (!camps.containsKey(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    return camps.get(username);
  }

  public void setCamp(String username, Camp camp) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(camp);
    if (!camps.containsKey(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    camps.put(username, camp);
  }

  public void discardAll(List<Card> cards) {
    cards = List.copyOf(cards);
    stack.discardAll(cards);
  }

  private record AskAction(
    LocalGameState localGameState,
    Future<PlayerTurn> answer) {
    private static AskAction asking(Player.Handler handler, LocalGameState localGameState, ExecutorService executor) {
      return new AskAction(
        localGameState,
        executor.submit(() -> handler.buildTurn(PlayerTurn.builder(localGameState))));
    }
  }

  private boolean isFinished() {
    return stack.isEmpty();
  }

}
