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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public final class InGame implements WebSocketServerState {
  private static final String ALREADY_PLAYED = "ALREADY_PLAYED";

  private static String username(WebSocketConnection connection) {
    return connection.userData()
      .get(USERNAME_KEY);
  }

  public static void start(OpenConnections connections, GameOption option) throws InterruptedException, IOException {
    Objects.requireNonNull(connections);
    var handlers = connections.stream()
      .collect(Collectors.toUnmodifiableMap(InGame::username, WebSocketPlayerHandler::of));
    var playerOrder = new ArrayList<>(handlers.keySet());
    Collections.shuffle(playerOrder);
    System.out.println("Player evaluation order: " + playerOrder);
    Game.start(handlers, playerOrder, option);
  }

  public void onAction(WebSocketConnection connection, Action action) {
    var userdata = connection.userData();
    var queue = userdata.get(ACTION_QUEUE_KEY);
    if (queue == null) {
      var username = userdata.get(USERNAME_KEY);
      throw new IllegalStateException("action queue is null for player " + username);
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
