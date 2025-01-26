package fr.ecoders.zombie;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import fr.ecoders.lobby.Lobby;
import fr.ecoders.quarkus.SealedDeserializer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.UserData.TypedKey;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;

@WebSocket(path = "game/{username}")
public class WebSocketServer {
  private static final int MIN_PLAYER_COUNT = 2;
  private static final TypedKey<SynchronousQueue<Action>> ACTION_QUEUE_KEY = new TypedKey<>("action_queue");
  private static final TypedKey<String> USERNAME_KEY = TypedKey.forString("username");
  private final Object lock = new Object();
  @Inject
  WebSocketConnection connection;
  private final Lobby lobby = new Lobby((username, state) -> connection.broadcast()
    .sendTextAndAwait(new Event.LobbyEvent(username, state)));
  @Inject
  OpenConnections connections;
  private boolean isStarted = false;
  private Thread gameThread = null;

  private String username() {
    return connection.pathParam("username");
  }

  private void startGame() {
    synchronized (lock) {
      Map<String, Game.PlayerHandler> handlers = connections.stream()
        .collect(Collectors.toUnmodifiableMap(
          c -> c.userData()
            .get(USERNAME_KEY),
          c -> {
            var queue = c.userData()
              .get(ACTION_QUEUE_KEY);
            return (gs) -> {
              c.sendTextAndAwait(new Event.GameStateWrapper(gs));
              return Action.withGuard(gs, queue.take());
            };
          }));
      isStarted = true;
      gameThread = Thread.ofVirtual()
        .name("Game thread")
        .start(() -> {
          try {
            Game.start(handlers);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
    }
  }

  @RunOnVirtualThread
  @Blocking
  @OnOpen
  public void onOpen() {
    var username = username();
    synchronized (lock) {
      if (!lobby.connect(username)) {
        connection.sendTextAndAwait("NAME_ALREADY_USED");
        connection.closeAndAwait();
        return;
      }
      var u = connection.userData();
      u.put(ACTION_QUEUE_KEY, new SynchronousQueue<>());
      u.put(USERNAME_KEY, username);
    }
  }

  @OnTextMessage
  public void onMessage(Command cmd) {
    var username = username();
    switch (cmd) {
      case Command.ChatMessage(String text) -> connection.broadcast()
        .sendTextAndAwait(new Event.ChatMessage(username, text, Instant.now()));
      case Command.LobbyCommand.UNREADY -> {
        synchronized (lock) {
          lobby.unready(username);
        }
      }
      case Command.LobbyCommand.READY -> {
        synchronized (lock) {
          lobby.ready(username);
          var players = lobby.players();
          if (lobby.isEveryoneReady() && players.size() >= MIN_PLAYER_COUNT) {
            startGame();
          }
        }
      }

      case Command.ActionWrapper(Action action) -> {
        synchronized (lock) {
          if (!isStarted) {
            throw new IllegalStateException("Game not started");
          }
          var u = connection.userData();
          var queue = u.get(ACTION_QUEUE_KEY);
          if (!queue.offer(action)) {
            throw new IllegalStateException("Was not waiting for a player's action");
          }
        }
      }
    }
  }

  @OnClose
  public void onClose() {
    var username = username();
    synchronized (lock) {
      lobby.disconnect(username);
      if (connections.stream()
        .count() < MIN_PLAYER_COUNT) {
        connections.stream()
          .forEach(c -> {
            var u = c.userData();
            lobby.disconnect(u.get(USERNAME_KEY));
            c.closeAndAwait();
          });
        isStarted = false;

      }
    }
  }

  public enum State {
    WAITING_PLAYER,
    ON_GOING,
  }

  public sealed interface Event {
    enum GameProgress {
      STARTING,
      FINISHED
    }

    record ChatMessage(
      String username,
      String text,
      Instant timestamp) implements Event {}

    record LobbyEvent(
      String player,
      Lobby.PlayerState state) implements Event {
    }

    record GameStateWrapper(GameState state) implements Event {}
  }


  public sealed interface Command {
    enum LobbyCommand implements Command {
      READY,
      UNREADY
    }

    record ChatMessage(String text) implements Command {}

    record ActionWrapper(Action action) implements Command {}
  }

  @Singleton
  public static class CustomObjectMapperCustomizer implements ObjectMapperCustomizer {
    @Override
    public void customize(ObjectMapper mapper) {
      var classes = List.of(Event.class, Card.class);
      mapper.registerModule(new SimpleModule().setSerializerModifier(new BeanSerializerModifier() {
        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
          JsonSerializer<?> serializer) {
          var isSub = classes.stream()
            .anyMatch(c -> c.isAssignableFrom(beanDesc.getBeanClass()));
          return isSub ? new JsonSerializer<>() {
            @Override
            public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
              TypeSerializer typeSer) throws IOException {
              serialize(value, gen, serializers);
            }

            @Override
            public void serialize(Object value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
              var clazz = value.getClass();
              var type = clazz.getSimpleName();
              generator.writeStartObject();
              generator.writeStringField("type", type);
              generator.writeFieldName("value");
              @SuppressWarnings("unchecked")
              var s = (JsonSerializer<Object>) serializer;
              s.serialize(value, generator, provider);
              generator.writeEndObject();
            }
          } : serializer;
        }
      }));
      //      SealedSerializer.addSealedSerializer(Card.class, mapper);
      //      SealedSerializer.addSealedSerializer(Event.class, mapper);
      SealedDeserializer.addSealedDeserializer(Command.class, mapper);
      SealedDeserializer.addSealedDeserializer(Action.class, mapper);
    }
  }
}
