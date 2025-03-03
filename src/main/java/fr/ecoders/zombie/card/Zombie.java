package fr.ecoders.zombie.card;

import fr.ecoders.zombie.state.ResourceBank;
import java.util.Objects;

public record Zombie(
  String name,
  int count,
  ResourceBank bonusProduction
) implements Card {
  public Zombie {
    Objects.requireNonNull(name);
    if (count <= 0) {
      throw new IllegalArgumentException("count must be greater than 0");
    }
    Objects.requireNonNull(bonusProduction);
  }
}
