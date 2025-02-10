package fr.ecoders.zombie.server;

import fr.ecoders.zombie.LocalGameState;
import fr.ecoders.zombie.Player;
import fr.ecoders.zombie.PlayerTurn;
import fr.ecoders.zombie.server.PlayerCommand.Action;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import io.quarkus.websockets.next.WebSocketConnection;

public class WebSocketPlayerHandler implements Player.Handler {
  private final WebSocketConnection connection;

  private WebSocketPlayerHandler(WebSocketConnection connection) {
    this.connection = connection;
  }

  public static WebSocketPlayerHandler ofConnection(WebSocketConnection connection) {
    return new WebSocketPlayerHandler(connection);
  }

  @Override
  public PlayerTurn buildTurn(LocalGameState state) throws InterruptedException {
    var userData = connection.userData();
    var queue = userData.get(ACTION_QUEUE_KEY);

    if (connection.isClosed()) {
      throw new InterruptedException();
    }
    userData.put(ACTION_THREAD_KEY, Thread.currentThread());
    var turnBuilder = PlayerTurn.with();
    // TODO remove turn start packet
    connection.sendTextAndAwait(new ServerEvent.TurnStart(state));
    while (!turnBuilder.isDone()) {
      connection.sendTextAndAwait(new ServerEvent.TurnUpdate(state));
      state = switch (queue.take()) {
        case Action.Construct(int index) -> turnBuilder.construct(state, index);
        case Action.Search(int index) -> turnBuilder.search(state, index);
        case Action.DestroyBuilding(int index) -> turnBuilder.destroyBuilding(state, index);
        case Action.CancelSearch(int index) -> turnBuilder.cancelSearch(state, index);
      };
    }
    connection.sendTextAndAwait(new ServerEvent.TurnEnd(state));
    userData.remove(ACTION_THREAD_KEY);
    return turnBuilder.build();
  }
}
