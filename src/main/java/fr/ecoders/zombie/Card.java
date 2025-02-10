package fr.ecoders.zombie;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static fr.ecoders.zombie.ResourceBank.Resource.FOOD;
import static fr.ecoders.zombie.ResourceBank.Resource.FUEL;
import static fr.ecoders.zombie.ResourceBank.Resource.MATERIALS;
import static fr.ecoders.zombie.ResourceBank.Resource.MEDICINES;
import static fr.ecoders.zombie.ResourceBank.Resource.MILITARY;
import static fr.ecoders.zombie.ResourceBank.Resource.PEOPLE;
import static fr.ecoders.zombie.ResourceBank.Resource.WATER;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public sealed interface Card {
  Building CAMPING_TENT = new Building(
    "Camping tent",
    new ResourceBank(Map.of(MATERIALS, 1, FOOD, 1, WATER, 1)),
    new ResourceBank(Map.of(PEOPLE, 5, FOOD, -1, WATER, -1)),
    new ResourceBank(Map.of(MATERIALS, 1)));
  Building RAIN_COLLECTORS = new Building(
    "Rain collectors",
    new ResourceBank(Map.of(MATERIALS, 2)),
    new ResourceBank(Map.of(WATER, 2)),
    new ResourceBank(Map.of(MATERIALS, 1, WATER, 1)));
  Building VEGETABLE_GARDEN = new Building(
    "Vegetable garden",
    new ResourceBank(Map.of(PEOPLE, 1, MATERIALS, 1)),
    new ResourceBank(Map.of(PEOPLE, -1, FOOD, 2)),
    new ResourceBank(Map.of(FOOD, 1)));
  List<Card> CARDS = List.of(
    RAIN_COLLECTORS,
    RAIN_COLLECTORS,
    RAIN_COLLECTORS,
    CAMPING_TENT,
    CAMPING_TENT,
    CAMPING_TENT,
    VEGETABLE_GARDEN,
    VEGETABLE_GARDEN,
    VEGETABLE_GARDEN,

    new Building(
      "Farm",
      new ResourceBank(Map.of(PEOPLE, 2, MATERIALS, 2)),
      new ResourceBank(Map.of(PEOPLE, -2, FOOD, 4)),
      new ResourceBank(Map.of(MATERIALS, 1, FOOD, 1))),

    new Building(
      "Hunter hut",
      new ResourceBank(Map.of(PEOPLE, 1)),
      new ResourceBank(Map.of(PEOPLE, -1, FOOD, 2)),
      new ResourceBank(Map.of())),

    new Building(
      "Medical tent",
      new ResourceBank(Map.of(MATERIALS, 1, PEOPLE, 1)),
      new ResourceBank(Map.of(MATERIALS, -1, PEOPLE, -1, MEDICINES, 1)),
      new ResourceBank(Map.of())),

    new Building(
      "Watch tower",
      new ResourceBank(Map.of(PEOPLE, 1, MATERIALS, 2)),
      new ResourceBank(Map.of(PEOPLE, -1, MILITARY, 1)),
      new ResourceBank(Map.of())),

    new Building(
      "Logging camp",
      new ResourceBank(Map.of(PEOPLE, 1)),
      new ResourceBank(Map.of(PEOPLE, -1, MATERIALS, 2)),
      new ResourceBank(Map.of())),

    new Building(
      "Ammunition workbench",
      new ResourceBank(Map.of(PEOPLE, 1, MATERIALS, 2)),
      new ResourceBank(Map.of(PEOPLE, -1, MATERIALS, -1, MILITARY, 2)),
      new ResourceBank(Map.of())),

    new Building(
      "Gas station",
      new ResourceBank(Map.of(MILITARY, 2, MATERIALS, 2)),
      new ResourceBank(Map.of(FUEL, 3)),
      new ResourceBank(Map.of(FUEL, 2, FOOD, 1, WATER, 1))),

    new Building(
      "Super market",
      new ResourceBank(Map.of(MILITARY, 1, FOOD, 2, WATER, 2, MATERIALS, 2)),
      new ResourceBank(Map.of(PEOPLE, 12, FOOD, -2, WATER, -2)),
      new ResourceBank(Map.of(FUEL, 1, FOOD, 1, WATER, 1, MATERIALS, 1))),

    new Building(
      "Police station",
      new ResourceBank(Map.of(MILITARY, 1, MATERIALS, 3)),
      new ResourceBank(Map.of(PEOPLE, -1, MILITARY, 2)),
      new ResourceBank(Map.of(MATERIALS, 1, FUEL, 1, MILITARY, 1)))
  );

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
    }
  }

}
