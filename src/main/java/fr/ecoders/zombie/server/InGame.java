package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.Camp;
import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.Game;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.MIN_PLAYER_COUNT;
import static fr.ecoders.zombie.server.WebSocketServer.USERNAME_KEY;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public final class InGame implements WebSocketServerState {
  private static final String ALREADY_PLAYED = "ALREADY_PLAYED";

  private static Game.PlayerInfo player(WebSocketConnection connection, Camp camp) {
    var queue = new SynchronousQueue<Action>();
    var userdata = connection.userData();
    var username = userdata.get(USERNAME_KEY);
    userdata.put(ACTION_QUEUE_KEY, queue);
    var handler = new WebSocketPlayerHandler(connection);
    return new Game.PlayerInfo(username, camp, handler);
  }

  public static void start(OpenConnections connections, List<Card> cards) throws InterruptedException, IOException {
    Objects.requireNonNull(connections);
    var buildings = cards.stream()
      .map(c -> c instanceof Building b ? b : null)
      .filter(Objects::nonNull)
      .toList();
    var vegetableGarden = buildings.stream()
      .filter(c -> "Vegetable gardens".equals(c.name()))
      .findAny()
      .orElseThrow();
    var campingTent = buildings.stream()
      .filter(c -> "Camping tents".equals(c.name()))
      .findAny()
      .orElseThrow();
    var rainCollectors = buildings.stream()
      .filter(c -> "Rain collectors".equals(c.name()))
      .findAny()
      .orElseThrow();
    var startingCamp = new Camp(6, List.of(vegetableGarden, campingTent, rainCollectors), List.of(), List.of());
    var players = connections.stream()
      .map(c -> player(c, startingCamp))
      .toList();
    Game.start(players, MIN_PLAYER_COUNT, cards);
  }

  public void onAction(WebSocketConnection connection, Action action) {
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
