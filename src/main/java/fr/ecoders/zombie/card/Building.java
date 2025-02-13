package fr.ecoders.zombie.card;

import fr.ecoders.zombie.ResourceBank;
import java.util.Objects;

public record Building(
  String name,
  ResourceBank cost,
  ResourceBank production,
  ResourceBank search
) implements Card.Searchable, Card.Buildable {
  public Building {
    Objects.requireNonNull(name);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(production);
    Objects.requireNonNull(search);
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }
    var productionCost = production.filterNegative()
      .multiply(-1);
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }
}
