package fr.ecoders.zombie;

import java.util.Objects;

public sealed interface Card {

  sealed interface Searchable extends Card {
    ResourceBank search();
  }

  sealed interface Buildable extends Card {
    ResourceBank cost();

    ResourceBank production();
  }


  record Building(
    String name,
    ResourceBank cost,
    ResourceBank production,
    ResourceBank search
  ) implements Searchable, Buildable {
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

}
