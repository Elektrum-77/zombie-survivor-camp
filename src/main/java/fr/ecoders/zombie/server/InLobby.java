package fr.ecoders.zombie.server;

import fr.ecoders.lobby.Lobby;
import static fr.ecoders.zombie.server.WebSocketServer.MIN_PLAYER_COUNT;
import static fr.ecoders.zombie.server.WebSocketServer.USERNAME_KEY;
import io.quarkus.websockets.next.WebSocketConnection;

public final class InLobby implements WebSocketServerState {
  private static final String NAME_ALREADY_USED = "NAME_ALREADY_USED";
  private final Lobby lobby = new Lobby();
  private final Object lock = new Object();

  public void onOpen(WebSocketConnection connection, String username) {
    synchronized (lock) {
      System.out.println("[InLobby] " + username + " connecting");
      connection.sendTextAndAwait(new ServerEvent.ConnectedPlayers(lobby.players()));
      if (!lobby.connect(username)) {
        System.out.println("[InLobby] " + username + " already connected");
        connection.sendTextAndAwait(NAME_ALREADY_USED);
        connection.closeAndAwait();
        return;
      }
      System.out.println("[InLobby] " + username + " connected");
      connection.userData()
        .put(USERNAME_KEY, username);
      connection.broadcast()
        .sendTextAndAwait(new ServerEvent.LobbyEvent(username, ServerEvent.LobbyEvent.State.CONNECT));
    }
  }

  public void waitStart() throws InterruptedException {
    synchronized (lock) {
      while (!lobby.isEveryoneReadyAndCount(MIN_PLAYER_COUNT)) {
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
          .sendTextAndAwait(new ServerEvent.LobbyEvent(
            username, switch (command) {
            case READY -> ServerEvent.LobbyEvent.State.READY;
            case UNREADY -> ServerEvent.LobbyEvent.State.UNREADY;
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
        .sendTextAndAwait(new ServerEvent.LobbyEvent(username, ServerEvent.LobbyEvent.State.DISCONNECT));
    }
  }
}
