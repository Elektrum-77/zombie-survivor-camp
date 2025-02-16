package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Player;
import fr.ecoders.zombie.PlayerTurn;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import io.quarkus.websockets.next.WebSocketConnection;
import java.util.Objects;

public class WebSocketPlayerHandler implements Player.Handler {
  private final WebSocketConnection connection;

  public WebSocketPlayerHandler(WebSocketConnection connection) {
    Objects.requireNonNull(connection);
    this.connection = connection;
  }

  public static WebSocketPlayerHandler ofConnection(WebSocketConnection connection) {
    return new WebSocketPlayerHandler(connection);
  }

  @Override
  public PlayerTurn buildTurn(PlayerTurn.Builder turnBuilder) throws InterruptedException {
    var userData = connection.userData();
    var queue = userData.get(ACTION_QUEUE_KEY);

    if (connection.isClosed()) {
      throw new InterruptedException();
    }
    userData.put(ACTION_THREAD_KEY, Thread.currentThread());
    // TODO remove turn start packet
    var state = turnBuilder.state();
    while (!turnBuilder.isDone()) {
      connection.sendTextAndAwait(new ServerEvent.TurnUpdate(state));
      state = turnBuilder.add(queue.take());
    }
    connection.sendTextAndAwait(new ServerEvent.TurnEnd(state));
    userData.remove(ACTION_THREAD_KEY);
    return turnBuilder.build();
  }
}
