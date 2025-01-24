package fr.ecoders.lobby;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

public final class Lobby {
  private final HashSet<String> players = new HashSet<>();
  private final HashSet<String> ready = new HashSet<>();
  private final BiConsumer<? super String, ? super PlayerState> onUpdate;

  public Lobby(BiConsumer<? super String, ? super PlayerState> onUpdate) {
    this.onUpdate = onUpdate;
  }

  public void disconnect(String username) {
    players.remove(username);
    onUpdate.accept(username, PlayerState.DISCONNECT);
  }

  public List<String> players() {
    return List.copyOf(players);
  }

  public boolean isEveryoneReady() {
    return ready.size() == players.size();
  }

  /// returns false if a user already uses this username
  public boolean connect(String username) {
    if (players.add(username)) {
      onUpdate.accept(username, PlayerState.CONNECT);
      return true;
    }
    return false;
  }

  public void ready(String username) {
    if (!players.contains(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    if (ready.add(username)) {
      onUpdate.accept(username, PlayerState.READY);
    }
  }

  public void unready(String username) {
    if (!players.contains(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    if (ready.remove(username)) {
      onUpdate.accept(username, PlayerState.UNREADY);
    }
  }

  public enum PlayerState {
    CONNECT,
    READY,
    UNREADY,
    DISCONNECT
  }


}
