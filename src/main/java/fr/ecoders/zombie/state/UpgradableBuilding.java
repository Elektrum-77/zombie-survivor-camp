package fr.ecoders.zombie.state;

import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Upgrade;
import static fr.ecoders.zombie.state.ResourceBank.sumAll;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record UpgradableBuilding(
  Building building,
  List<Upgrade> upgrades) {
  public UpgradableBuilding {
    Objects.requireNonNull(building);
    upgrades = List.copyOf(upgrades);
  }

  public static UpgradableBuilding ofBuilding(Building building) {
    // implicit check
    return new UpgradableBuilding(building, List.of());
  }

  public UpgradableBuilding withUpgrades(List<Upgrade> upgrades) {
    // implicit check
    return new UpgradableBuilding(building, upgrades);
  }

  private Stream<Upgrade> upgradeStream() {
    return upgrades.stream();
  }

  public ResourceBank production() {
    var production = sumAll(upgradeStream().map(Upgrade::production)).addAll(building.production());
    var isElectrified = upgradeStream().anyMatch(Upgrade::isPowerGenerator);
    return building.electrified()
      .filter(_ -> isElectrified)
      .map(production::addAll)
      .orElse(production);
  }
}
