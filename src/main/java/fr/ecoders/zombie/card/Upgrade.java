package fr.ecoders.zombie.card;

import static fr.ecoders.zombie.card.Card.validateName;
import fr.ecoders.zombie.state.ResourceBank;
import fr.ecoders.zombie.state.UpgradableBuilding;
import java.util.Objects;
import java.util.function.BiPredicate;

public record Upgrade(
  String name,
  ResourceBank cost,
  ResourceBank production,
  boolean isPowerGenerator,
  Filter filter
) implements Card {
  public Upgrade {
    name = validateName(name);
    Objects.requireNonNull(production);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(filter);
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }
    var productionCost = production.cost();
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }

  public boolean isUpgradable(UpgradableBuilding ub) {
    var isNotElectrifiable = ub.building()
      .electrified()
      .isEmpty();
    if (isPowerGenerator && isNotElectrifiable) {
      return false;
    }
    return filter.test(this, ub);
  }

  public sealed interface Filter extends BiPredicate<Upgrade, UpgradableBuilding> {
    Filter NONE = new Const((_, _) -> true);
    Filter UNIQUE = new Const((u, ub) -> !ub.upgrades()
      .contains(u));

    final class Const implements Filter {
      private final BiPredicate<Upgrade, UpgradableBuilding> impl;

      private Const(BiPredicate<Upgrade, UpgradableBuilding> impl) {
        this.impl = impl;
      }

      @Override
      public boolean test(Upgrade upgrade, UpgradableBuilding building) {
        return impl.test(upgrade, building);
      }
    }

    record IsCategory(Building.Category category) implements Filter {
      public IsCategory {
        Objects.requireNonNull(category);
      }

      @Override
      public boolean test(Upgrade upgrade, UpgradableBuilding upgradableBuilding) {
        return upgradableBuilding.building()
          .category()
          .filter(category::equals)
          .isPresent();
      }
    }
  }
}
