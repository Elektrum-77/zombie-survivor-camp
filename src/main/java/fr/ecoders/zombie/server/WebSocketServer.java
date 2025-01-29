package fr.ecoders.zombie.server;

import fr.ecoders.zombie.server.WebSocketServerState.InGame;
import fr.ecoders.zombie.server.WebSocketServerState.InLobby;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.UserData.TypedKey;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Logger;

@Singleton
@WebSocket(path = "game/{username}")
public class WebSocketServer {
  private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class.getName());
  public static final int MIN_PLAYER_COUNT = 1;
  static final TypedKey<Thread> ACTION_THREAD_KEY = new TypedKey<>("action_thread");
  static final TypedKey<SynchronousQueue<PlayerCommand.Action>> ACTION_QUEUE_KEY = new TypedKey<>("action_queue");
  static final TypedKey<String> USERNAME_KEY = TypedKey.forString("username");
  private final Object lock = new Object();
  volatile private WebSocketServerState state;

  @Inject
  WebSocketConnection connection;
  @Inject
  OpenConnections connections;

  public WebSocketServer() {
  }

  void onStart(@Observes StartupEvent e) {
    Thread.ofVirtual()
      .start(() -> {
        try {
          while (!Thread.interrupted()) {
            LOGGER.info("Starting");
            var inLobby = new InLobby();
            state = inLobby;
            LOGGER.info("In lobby");
            inLobby.waitStart();
            state = new InGame();
            LOGGER.info("In game");
            try {
              InGame.start(connections);
            } catch (AssertionError ae) { /* reset server state */ }
          }
        } catch (InterruptedException ex) { /* exit thread */ }
      });
  }

  private String username() {
    return connection.pathParam("username");
  }

  @RunOnVirtualThread
  @Blocking
  @OnOpen
  public void onOpen() {
    var username = username();
    synchronized (lock) {
      switch (state) {
        case InGame _ -> {
          connection.sendTextAndAwait("_" + connections.listAll().size() + "_players_connected");
          connection.sendTextAndAwait("GAME_ALREADY_STARTED");
          connection.closeAndAwait();
        }
        case InLobby inLobby -> inLobby.onOpen(connections, connection, username);
        case null -> connection.sendTextAndAwait("SERVER_IS_STARTING");
      }
    }
  }

  @RunOnVirtualThread
  @Blocking
  @OnTextMessage
  public void onMessage(PlayerCommand command) {
    var username = username();
    switch (command) {
      case PlayerCommand.ChatMessage(String text) -> connection.broadcast()
        .sendTextAndAwait(new ServerEvent.ChatMessage(username, text, Instant.now()));
      case PlayerCommand.Action action when state instanceof InGame inGame ->
        inGame.onAction(connection, action);
      case PlayerCommand.LobbyCommand cmd when state instanceof InLobby inLobby -> inLobby.onMessage(connection, cmd);
      default -> {
        connection.closeAndAwait();
        throw new IllegalStateException("Player " + username + " sent " + command + " at the wrong time");
      }
    }
  }

  @RunOnVirtualThread
  @Blocking
  @OnClose
  public void onClose() {
    switch (state) {
      case InLobby inLobby -> inLobby.onClose(connection);
      case InGame inGame -> inGame.onClose(connection);
      case null -> { /* do nothing */ }
    }
  }


}
