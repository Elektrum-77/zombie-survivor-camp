package fr.ecoders.zombie.card;

import fr.ecoders.zombie.state.ResourceBank;
import java.util.Objects;
import java.util.Optional;

public record Building(
  String name,
  ResourceBank cost,
  ResourceBank production,
  Optional<ResourceBank> electrified,
  Optional<Category> category,
  ResourceBank search
) implements Card.Searchable, Card {
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

  public enum Category {
    AGRICULTURAL, STOCKAGE, WORKBENCH, WATCH_TOWER
  }
}
