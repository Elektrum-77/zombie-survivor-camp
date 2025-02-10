package fr.ecoders.zombie.server;

import fr.ecoders.zombie.LocalGameState;
import fr.ecoders.zombie.Player;
import fr.ecoders.zombie.PlayerTurn;
import fr.ecoders.zombie.server.PlayerCommand.Action;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import io.quarkus.websockets.next.WebSocketConnection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public class WebSocketPlayerHandler implements Player.Handler {
  private final WebSocketConnection connection;
  private final SynchronousQueue<Action> queue;

  public static WebSocketPlayerHandler ofConnection(WebSocketConnection connection) {
    var userData = connection.userData();
    var queue = Objects.requireNonNull(userData.get(ACTION_QUEUE_KEY));
    return new WebSocketPlayerHandler(connection, queue);
  }

  private WebSocketPlayerHandler(WebSocketConnection connection, SynchronousQueue<Action> queue) {
    this.connection = connection;
    this.queue = queue;
  }


  @Override
  public PlayerTurn buildTurn(LocalGameState state) throws InterruptedException {
    if (connection.isClosed()) {
      throw new InterruptedException();
    }
//    userdata.put(ACTION_THREAD_KEY, Thread.currentThread());
    var turnBuilder = PlayerTurn.with();
    connection.sendTextAndAwait(new ServerEvent.TurnStart(state));
    while (!turnBuilder.isDone()) {
      state = switch (queue.take()) {
        case Action.Construct(int index) -> turnBuilder.construct(state, index);
        case Action.Search(int index) -> turnBuilder.search(state, index);
        case Action.DestroyBuilding(int index) -> turnBuilder.destroyBuilding(state, index);
        case Action.CancelSearch(int index) -> turnBuilder.cancelSearch(state, index);
      };
    }
    connection.sendTextAndAwait(new ServerEvent.TurnEnd(state));
//    userdata.remove(ACTION_THREAD_KEY);
    return turnBuilder.build();
  }
}
