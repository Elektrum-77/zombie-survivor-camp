package fr.ecoders.zombie.card;

import java.util.Objects;

public record ZombieEvent(
  String name,
  int count) implements Card.Zombie {
  public ZombieEvent {
    Objects.requireNonNull(name);
    if (count <= 0) {
      throw new IllegalArgumentException("count must be greater than 0");
    }
  }
}
