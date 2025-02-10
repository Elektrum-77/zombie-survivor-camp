package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Camp;
import fr.ecoders.zombie.Card;
import fr.ecoders.zombie.Game;
import fr.ecoders.zombie.Player;
import fr.ecoders.zombie.PlayerTurn;
import static fr.ecoders.zombie.server.PlayerCommand.Action.*;
import fr.ecoders.zombie.server.PlayerCommand.Action.Construct;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.MIN_PLAYER_COUNT;
import static fr.ecoders.zombie.server.WebSocketServer.USERNAME_KEY;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public final class InGame implements WebSocketServerState {
  private static final String ALREADY_PLAYED = "ALREADY_PLAYED";

  private static Player.Handler handler(WebSocketConnection connection) {
    var userdata = connection.userData();
    var queue = userdata.get(ACTION_QUEUE_KEY);
    return state -> {
      if (connection.isClosed()) {
        throw new InterruptedException();
      }
      userdata.put(ACTION_THREAD_KEY, Thread.currentThread());
      var turnBuilder = PlayerTurn.with();
      connection.sendTextAndAwait(new ServerEvent.TurnStart(state));
      while (!turnBuilder.isDone()) {
        state = switch (queue.take()) {
          case Construct(int index) -> turnBuilder.construct(state, index);
          case Search(int index) -> turnBuilder.search(state, index);
          case DestroyBuilding(int index) -> turnBuilder.destroyBuilding(state, index);
          case CancelSearch(int index) -> turnBuilder.cancelSearch(state, index);
        };
      }
      connection.sendTextAndAwait(new ServerEvent.TurnEnd(state));
      userdata.remove(ACTION_THREAD_KEY);
      return turnBuilder.build();
    };
  }

  private static Game.PlayerInfo player(WebSocketConnection connection) {
    var queue = new SynchronousQueue<PlayerCommand.Action>();
    var userdata = connection.userData();
    var username = userdata.get(USERNAME_KEY);
    var camp = new Camp(6, List.of(Card.CAMPING_TENT, Card.RAIN_COLLECTORS, Card.VEGETABLE_GARDEN), List.of());
    userdata.put(ACTION_QUEUE_KEY, queue);
    var handler = handler(connection);
    return new Game.PlayerInfo(username, camp, handler);
  }

  public static void start(OpenConnections connections) throws InterruptedException {
    Objects.requireNonNull(connections);
    var players = connections.stream()
      .map(fr.ecoders.zombie.server.InGame::player)
      .toList();
    Game.start(players, MIN_PLAYER_COUNT);
  }

  public void onAction(WebSocketConnection connection, PlayerCommand.Action action) {
    var userdata = connection.userData();
    var queue = userdata.get(ACTION_QUEUE_KEY);
    if (queue == null) {
      throw new AssertionError("queue is null");
    }
    if (!queue.offer(action)) {
      connection.sendTextAndAwait(ALREADY_PLAYED);
    }
  }

  public void onClose(WebSocketConnection connection) {
    var userdata = connection.userData();
    var thread = userdata.get(ACTION_THREAD_KEY);
    if (thread != null) {
      thread.interrupt();
    }
  }
}
