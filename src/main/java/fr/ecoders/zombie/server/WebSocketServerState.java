package fr.ecoders.zombie.server;

import fr.ecoders.lobby.Lobby;
import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.Camp;
import fr.ecoders.zombie.Card;
import fr.ecoders.zombie.Game;
import fr.ecoders.zombie.Game.Player;
import fr.ecoders.zombie.server.ServerEvent.LobbyEvent;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_QUEUE_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.ACTION_THREAD_KEY;
import static fr.ecoders.zombie.server.WebSocketServer.MIN_PLAYER_COUNT;
import static fr.ecoders.zombie.server.WebSocketServer.USERNAME_KEY;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import static java.util.stream.Collectors.toUnmodifiableMap;

public sealed interface WebSocketServerState {
  final class InLobby implements WebSocketServerState {
    private static final String NAME_ALREADY_USED = "NAME_ALREADY_USED";
    private final Lobby lobby = new Lobby();
    private final Object lock = new Object();

    public void onOpen(OpenConnections connections, WebSocketConnection connection, String username) {
      synchronized (lock) {
        var players = connections.stream()
          .map(WebSocketConnection::userData)
          .map(u -> u.get(USERNAME_KEY))
          .filter(Objects::nonNull)
          .map(n -> new ServerEvent.ConnectedPlayerList.Player(n, lobby.isReady(n)))
          .toList();
        connection.sendTextAndAwait(new ServerEvent.ConnectedPlayerList(players));
        if (!lobby.connect(username)) {
          connection.sendTextAndAwait(NAME_ALREADY_USED);
        }
        connection.userData()
          .put(USERNAME_KEY, username);
        connection.broadcast()
          .sendTextAndAwait(new LobbyEvent(username, LobbyEvent.State.CONNECT));
      }
    }

    public void waitStart() throws InterruptedException {
      synchronized (lock) {
        while (MIN_PLAYER_COUNT > lobby.players()
          .size() || !lobby.isEveryoneReady()) {
          lock.wait();
        }
      }
    }

    public void onMessage(WebSocketConnection connection, PlayerCommand.LobbyCommand command) {
      synchronized (lock) {
        var userdata = connection.userData();
        var username = userdata.get(USERNAME_KEY);
        if (switch (command) {
          case READY -> lobby.ready(username);
          case UNREADY -> lobby.unready(username);
        }) {
          connection.broadcast()
            .sendTextAndAwait(new LobbyEvent(
              username, switch (command) {
              case READY -> LobbyEvent.State.READY;
              case UNREADY -> LobbyEvent.State.UNREADY;
            }));
          if (command == PlayerCommand.LobbyCommand.READY) {
            lock.notifyAll();
          }
        }
      }
    }

    public void onClose(WebSocketConnection connection) {
      var userdata = connection.userData();
      var username = userdata.get(USERNAME_KEY);
      if (username == null) {
        return;
      }
      synchronized (lock) {
        lobby.disconnect(username);
        connection.broadcast()
          .sendTextAndAwait(new LobbyEvent(username, LobbyEvent.State.DISCONNECT));
      }
    }
  }

  final class InGame implements WebSocketServerState {
    private static final String ALREADY_PLAYED = "ALREADY_PLAYED";

    private static Player player(WebSocketConnection connection) {
      var queue = new SynchronousQueue<Action>();
      var userdata = connection.userData();
      var username = userdata.get(USERNAME_KEY);
      var camp = new Camp(6, List.of(Card.CAMPING_TENT, Card.RAIN_COLLECTORS, Card.VEGETABLE_GARDEN), List.of());
      userdata.put(ACTION_QUEUE_KEY, queue);
      Player.Handler handler = gs -> {
        if (connection.isClosed()) {
          throw new InterruptedException();
        }
        userdata.put(ACTION_THREAD_KEY, Thread.currentThread());
        connection.sendTextAndAwait(new ServerEvent.GameStateWrapper(gs));
        var action = queue.take();
        userdata.remove(ACTION_THREAD_KEY);
        return action;
      };
      return new Player(username, camp, handler);
    }

    public static void start(OpenConnections connections) throws InterruptedException {
      Objects.requireNonNull(connections);
      var players = connections.stream()
        .collect(toUnmodifiableMap(
          c -> {
            var userdata = c.userData();
            return userdata.get(USERNAME_KEY);
          }, InGame::player));
      Game.start(players, MIN_PLAYER_COUNT);
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

}
