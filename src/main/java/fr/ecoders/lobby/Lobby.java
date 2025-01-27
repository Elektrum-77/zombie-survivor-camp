package fr.ecoders.lobby;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class Lobby {
  private final HashSet<String> players = new HashSet<>();
  private final HashSet<String> ready = new HashSet<>();

  public Map<String, Boolean> players() {
    return players.stream()
      .collect(toUnmodifiableMap(identity(), ready::contains));
  }

  public boolean isReady(String player) {
    Objects.requireNonNull(player);
    if (!players.contains(player)) {
      throw new IllegalStateException("Unknown player " + player);
    }
    return ready.contains(player);
  }

  public boolean isEveryoneReady() {
    return ready.size() == players.size();
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
