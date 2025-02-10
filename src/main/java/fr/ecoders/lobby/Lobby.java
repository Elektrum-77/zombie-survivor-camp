package fr.ecoders.lobby;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public final class Lobby {
  private final LinkedHashSet<String> players = new LinkedHashSet<>();
  private final HashSet<String> ready = new HashSet<>();

  public List<Player> players() {
    return players.stream()
      .map(username -> new Player(username, isReady(username)))
      .toList();
  }

  public record Player(
    String username,
    boolean isReady) {
    public Player {
      Objects.requireNonNull(username);
    }
  }

  public boolean isReady(String player) {
    Objects.requireNonNull(player);
    if (!players.contains(player)) {
      throw new IllegalStateException("Unknown player " + player);
    }
    return ready.contains(player);
  }

  public boolean isEveryoneReadyAndCount(int minPlayerSize) {
    return ready.size() == players.size() && players.size() >= minPlayerSize;
  }

  /// returns false if this username is already used
  public boolean connect(String username) {
    return players.add(username);
  }

  public void disconnect(String username) {
    players.remove(username);
  }

  /// returns true if the user's state has changed
  public boolean ready(String username) {
    if (!players.contains(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    return ready.add(username);
  }

  /// returns true if the user's state has changed
  public boolean unready(String username) {
    if (!players.contains(username)) {
      throw new IllegalArgumentException("Player " + username + " does not exist");
    }
    return ready.remove(username);
  }
}
