package fr.ecoders.zombie.card;

import static fr.ecoders.zombie.card.Card.validateName;
import fr.ecoders.zombie.state.ResourceBank;
import fr.ecoders.zombie.state.UpgradableBuilding;
import java.util.Objects;
import java.util.Optional;

public record Upgrade(
  String name,
  ResourceBank cost,
  ResourceBank production,
  boolean isPowerGenerator,
  boolean isUnique,
  Optional<Building.Category> categoryFilter
) implements Card {
  public Upgrade {
    name = validateName(name);
    Objects.requireNonNull(production);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(categoryFilter);
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }
    var productionCost = production.cost();
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }

  public boolean isUpgradable(UpgradableBuilding ub) {
    if (isUnique && ub.upgrades()
      .contains(this)) {
      return false;
    }
    if (isPowerGenerator && ub.upgrades()
      .stream()
      .anyMatch(Upgrade::isPowerGenerator)) {
      return false;
    }
    return categoryFilter.map(category -> ub.building()
        .category()
        .map(category::equals)
        .orElse(false))
      .orElse(true);
  }

}
