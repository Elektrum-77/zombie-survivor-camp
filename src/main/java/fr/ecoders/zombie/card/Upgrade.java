package fr.ecoders.zombie.card;

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
    Objects.requireNonNull(name);
    Objects.requireNonNull(production);
    Objects.requireNonNull(cost);
    Objects.requireNonNull(filter);

    var productionCost = production.cost();
    if (!cost.containsAll(productionCost)) {
      cost = cost.addAll(productionCost);
    }
  }

  public ResourceBank cost() {
    return cost.addAll(production.cost());
  }

  public boolean isUpgradable(UpgradableBuilding ub) {
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
