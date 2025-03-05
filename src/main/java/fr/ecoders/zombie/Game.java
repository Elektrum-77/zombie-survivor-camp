package fr.ecoders.zombie;

import fr.ecoders.zombie.state.GameState;
import fr.ecoders.zombie.state.Player;
import static java.lang.Thread.currentThread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Game {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private final HashMap<String, PlayerHandler> handlers;
  private final ArrayList<String> playerOrder;
  private final GameOption option;
  private GameState state;

  private Game(HashMap<String, PlayerHandler> handlers, ArrayList<String> playerOrder, GameOption option,
    GameState state) {
    this.handlers = handlers;
    this.playerOrder = playerOrder;
    this.option = option;
    this.state = state;
  }


  public static void start(Map<String, ? extends PlayerHandler> handlers, List<String> playerOrder, GameOption option) {
    handlers = Map.copyOf(handlers);
    playerOrder = List.copyOf(playerOrder);
    Objects.requireNonNull(option);

    var shuffledCards = new ArrayList<>(option.cards());
    Collections.shuffle(shuffledCards);

    var players = playerOrder.stream()
      .collect(Collectors.toUnmodifiableMap(Function.identity(), _ -> new Player(option.baseCamp(), List.of())));
    var initialState = new GameState(option, players, shuffledCards, List.of());
    var game = new Game(new HashMap<>(handlers), new ArrayList<>(playerOrder), option, initialState);
    game.play();
  }

  private void play() {
    state = state.startUp();
    for (int phase = 0; phase < option.phaseCount() && hasEnoughPlayer(); phase++) {
      playPhase(phase);
    }
  }

  private boolean hasEnoughPlayer() {
    return playerOrder.size() >= option.minPlayerCount();
  }

  private void playRound(int round) {
    System.out.println("Starting round " + (round + 1) + " of " + option.phaseTurnCount());
    var lock = new Object();
    var turns = new HashMap<String, PlayerTurn>();

    synchronized (lock) {
      // ask players what to do
      var threads = playerOrder.stream()
        .map(player -> {
          var handler = handlers.get(player);
          return Thread.ofVirtual()
            .name("Turn handler of " + player)
            .start(() -> {
              try {
                var turn = handler.buildTurn(PlayerTurn.builder(playerOrder, state, player));
                synchronized (lock) {
                  turns.put(player, turn);
                  lock.notifyAll();
                }
              } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Player " + player + " disconnected due to InterruptedException.", e);
              } finally {
                if (!turns.containsKey(player)) {
                  handler.kick();
                  synchronized (lock) {
                    handlers.remove(player);
                    playerOrder.remove(player);
                    lock.notifyAll();
                  }
                }
              }
            });
        })
        .toList();

      // waits all player
      while (turns.size() != playerOrder.size()) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          LOGGER.log(Level.WARNING, "Interrupting all handlers threads due to InterruptedException.", e);
          threads.forEach(Thread::interrupt);
          currentThread().interrupt();
        }
      }

      if (!hasEnoughPlayer()) {
        return;
      }

      // process players' turn
      for (var player : playerOrder) {
        var turn = turns.get(player);
        state = turn.play(state, player);
      }
    }

    state = state.cleanUp();
  }

  private void playPhase(int phase) {
    System.out.println("Starting phase " + (phase + 1) + " of " + option.phaseCount());
    for (int i = 0; i < option.phaseTurnCount() && hasEnoughPlayer(); i++) {
      playRound(i);
    }
  }
}
