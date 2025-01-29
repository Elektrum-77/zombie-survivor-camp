package fr.ecoders.zombie;

import java.util.Objects;

public record Player(
  String name,
  Camp camp,
  Handler handler) {
  public Player {
    Objects.requireNonNull(name);
    Objects.requireNonNull(camp);
    Objects.requireNonNull(handler);
  }

  @FunctionalInterface
  public interface Handler {
    PlayerTurn buildTurn(LocalGameState gs) throws InterruptedException;
  }
}
