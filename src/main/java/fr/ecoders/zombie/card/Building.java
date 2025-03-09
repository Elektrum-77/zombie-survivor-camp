package fr.ecoders.zombie.card;

import static fr.ecoders.zombie.card.Card.validateName;
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
) implements Card {
  public Building {
    name = validateName(name);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(production);
    Objects.requireNonNull(search);
    var productionCost = production.cost();
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }

  public enum Category {
    AGRICULTURAL, STORAGE, WORKBENCH, WATCH_TOWER
  }
}
