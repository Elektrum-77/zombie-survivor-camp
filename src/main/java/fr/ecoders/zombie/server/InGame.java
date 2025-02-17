package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.Game;
import fr.ecoders.zombie.GameOption;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.USERNAME_KEY;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public final class InGame implements WebSocketServerState {
  private static final String ALREADY_PLAYED = "ALREADY_PLAYED";

  private static Game.PlayerInfo player(WebSocketConnection connection) {
    var queue = new SynchronousQueue<Action>();
    var userdata = connection.userData();
    var username = userdata.get(USERNAME_KEY);
    userdata.put(ACTION_QUEUE_KEY, queue);
    var handler = new WebSocketPlayerHandler(connection);
    return new Game.PlayerInfo(username, handler);
  }

  public static void start(OpenConnections connections, GameOption option) throws InterruptedException, IOException {
    Objects.requireNonNull(connections);
    var players = connections.stream()
      .map(InGame::player)
      .toList();
    Game.start(players, option);
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
