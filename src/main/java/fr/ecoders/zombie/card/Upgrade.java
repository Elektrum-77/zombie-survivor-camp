package fr.ecoders.zombie.card;

import fr.ecoders.zombie.state.ResourceBank;
import java.util.Objects;
import java.util.Set;

public record Upgrade(ResourceBank production, boolean isPowerGenerator, Set<Building.Category> filter) implements Card {
  public Upgrade {
    Objects.requireNonNull(production);
  }

  public ResourceBank cost() {
    return production.cost();
  }
}
