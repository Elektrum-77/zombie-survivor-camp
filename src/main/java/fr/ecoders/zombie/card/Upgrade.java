package fr.ecoders.zombie.card;

import fr.ecoders.zombie.state.ResourceBank;
import java.util.Objects;
import java.util.Set;

public record Upgrade(
  String name,
  ResourceBank cost,
  ResourceBank production,
  boolean isPowerGenerator,
  Set<Building.Category> filter
) implements Card {
  public Upgrade {
    Objects.requireNonNull(production);
    Objects.requireNonNull(cost);
    filter = Set.copyOf(filter);

    var productionCost = production.cost();
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }

  public ResourceBank cost() {
    return cost.addAll(production.cost());
  }
}
