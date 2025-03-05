package fr.ecoders.zombie.server;

import fr.ecoders.zombie.PlayerHandler;
import fr.ecoders.zombie.PlayerTurn;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import io.quarkus.websockets.next.WebSocketConnection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;

public class WebSocketPlayerHandler implements PlayerHandler {
  private final WebSocketConnection connection;

  private WebSocketPlayerHandler(WebSocketConnection connection) {
    Objects.requireNonNull(connection);
    this.connection = connection;
  }

  public static WebSocketPlayerHandler of(WebSocketConnection connection) {
    var userdata = connection.userData();
    userdata.put(ACTION_QUEUE_KEY, new SynchronousQueue<>(true));
    return new WebSocketPlayerHandler(connection);
  }

  @Override
  public PlayerTurn buildTurn(PlayerTurn.Builder turnBuilder) throws InterruptedException {
    Objects.requireNonNull(turnBuilder);
    var userData = connection.userData();
    var queue = userData.get(ACTION_QUEUE_KEY);

    if (connection.isClosed()) {
      throw new InterruptedException();
    }
    userData.put(ACTION_THREAD_KEY, Thread.currentThread());

    try {
      while (!turnBuilder.isDone()) {
        var state = turnBuilder.localState();
        var event = new ServerEvent.TurnUpdate(state);
        connection.sendTextAndAwait(event);

        var action = queue.take();
        turnBuilder.add(action);
      }
      var state = turnBuilder.localState();
      var event = new ServerEvent.TurnEnd(state);
      connection.sendTextAndAwait(event);

      return turnBuilder.build();
    } finally {
      userData.remove(ACTION_THREAD_KEY);
    }
  }

  @Override
  public void kick() {
    var userdata = connection.userData();
    var threadOpt = Optional.ofNullable(userdata.get(ACTION_THREAD_KEY));
    threadOpt.ifPresent(Thread::interrupt);
    connection.closeAndAwait();
  }
}
