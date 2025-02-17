package fr.ecoders.zombie.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ecoders.zombie.Action;
import fr.ecoders.zombie.GameOption;
import fr.ecoders.zombie.GameOption.CardOption;
import fr.ecoders.zombie.server.PlayerCommand.ActionWrapper;
import fr.ecoders.zombie.server.PlayerCommand.LobbyCommand;
import io.quarkus.runtime.Quarkus;
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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@WebSocket(path = "game/{username}")
@RunOnVirtualThread
public class WebSocketServer {
  private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class.getName());
  public static final int MIN_PLAYER_COUNT = 1;
  static final TypedKey<Thread> ACTION_THREAD_KEY = new TypedKey<>("action_thread");
  static final TypedKey<SynchronousQueue<Action>> ACTION_QUEUE_KEY = new TypedKey<>("action_queue");
  static final TypedKey<String> USERNAME_KEY = TypedKey.forString("username");
  private final Object lock = new Object();
  volatile private WebSocketServerState state;

  @Inject
  ObjectMapper mapper;
  @Inject
  WebSocketConnection connection;
  @Inject
  OpenConnections connections;

  public WebSocketServer() {
  }

  private List<CardOption> readCards(String path) throws IOException {
    try {
      return Optional.ofNullable(getClass().getResourceAsStream(path))
        .<List<CardOption>>map(
          inputStream -> {
            try (var parser = mapper.createParser(new BufferedReader(new InputStreamReader(inputStream)))) {
              return List.copyOf(parser.readValueAs(new TypeReference<List<CardOption>>() {}));
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          })
        .orElseThrow(() -> new FileNotFoundException("Cards file '" + path + "' not found"));
    } catch (UncheckedIOException e) {
      throw e.getCause();
    }
  }

  private GameOption readGameOption() throws IOException {
    var cardPath = "/assets/cards.json";
    return GameOption.defaultOption(readCards(cardPath));
  }

  void onStart(@Observes StartupEvent e) {
    Thread.ofVirtual()
      .name("server thread")
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
              InGame.start(connections, readGameOption());
            } catch (RuntimeException ae) {
              LOGGER.log(Level.WARNING, "Restarting game due to RuntimeException", ae);
            }
          }
        } catch (InterruptedException ex) { /* exit thread */ } catch (IOException ex) {
          LOGGER.log(Level.SEVERE, "Closing server due to IOException", ex);
          Quarkus.asyncExit();
        }
      });
  }

  private String username() {
    return connection.pathParam("username");
  }

  private void broadcastChatMessage(String username, String text) {
    connection.broadcast()
      .sendTextAndAwait(new ServerEvent.ChatMessage(username, text, Instant.now()));
  }

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
        case InLobby inLobby -> inLobby.onOpen(connection, username);
        case null -> connection.sendTextAndAwait("SERVER_IS_STARTING");
      }
    }
  }

  @Blocking
  @OnTextMessage
  public void onMessage(PlayerCommand command) {
    var username = username();
    synchronized (lock) {
      switch (command) {
        case PlayerCommand.ChatMessage(String text) -> broadcastChatMessage(username, text);
        case ActionWrapper(Action action) when state instanceof InGame inGame -> inGame.onAction(connection, action);
        case LobbyCommand cmd when state instanceof InLobby inLobby -> inLobby.onMessage(connection, cmd);
        case ActionWrapper _, LobbyCommand _ -> {
          connection.closeAndAwait();
          throw new IllegalStateException(
            "Player " + username + " sent " + command + " at the wrong time (" + state + ")");
        }
      }
    }
  }

  @Blocking
  @OnClose
  public void onClose() {
    synchronized (lock) {
      switch (state) {
        case InLobby inLobby -> inLobby.onClose(connection);
        case InGame inGame -> inGame.onClose(connection);
        case null -> { /* do nothing */ }
      }
    }
  }


}
