package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.card.Upgrade;
import fr.ecoders.zombie.state.Camp;
import fr.ecoders.zombie.state.ResourceBank;
import fr.ecoders.zombie.state.UpgradableBuilding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface GameOption {
  static GameOption defaultOption(List<CardOption> cardOptions) {
    cardOptions = List.copyOf(cardOptions);

    record Impl(
      int phaseCount,
      int phaseTurnCount,
      int baseDrawCount,
      int minPlayerCount,
      Camp baseCamp,
      List<Card> cards
    ) implements GameOption {}

    var cards = cardOptions.stream()
      .map(opt -> Collections.nCopies(opt.replica(), opt.card()))
      .flatMap(Collection::stream)
      .toList();
    var buildings = cards.stream()
      .map(c -> c instanceof Building b ? b : null)
      .filter(Objects::nonNull)
      .toList();
    var campingTents = buildings.stream()
      .filter(b -> "Camping tents".equals(b.name()))
      .findAny()
      .orElseThrow();
    var rainCollectors = buildings.stream()
      .filter(b -> "Rain collectors".equals(b.name()))
      .findAny()
      .orElseThrow();
    var vegetableGardens = buildings.stream()
      .filter(b -> "Vegetable gardens".equals(b.name()))
      .findAny()
      .orElseThrow();

    var starterBuildings = Stream.of(campingTents, rainCollectors, vegetableGardens)
      .map(UpgradableBuilding::ofBuilding)
      .collect(Collectors.toCollection(ArrayList::new));
    starterBuildings.set(
      0,
      starterBuildings.getFirst()
        .withUpgrades(List.of(
          new Upgrade(
            "Perfect solar panels",
            ResourceBank.EMPTY,
            ResourceBank.EMPTY,
            true,
            false,
            Optional.empty()
          ), new Upgrade(
            "Additional sleeping bag",
            ResourceBank.EMPTY,
            new ResourceBank(Map.of(Resource.PEOPLE, 1)),
            false,
            false,
            Optional.empty()
          ))));
    var camp = new Camp(6, starterBuildings, List.of(), List.of(), ResourceBank.EMPTY);
    return new Impl(5, 15, 3, 1, camp, cards);
  }

  int phaseCount();

  int phaseTurnCount();

  int baseDrawCount();

  int minPlayerCount();

  Camp baseCamp();

  List<Card> cards();

  record CardOption(
    Card card,
    int replica) {
    public CardOption {
      Objects.requireNonNull(card);
      if (replica <= 0) {
        throw new IllegalArgumentException("replica must be greater than 0");
      }
    }
  }

}
