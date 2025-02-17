package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.card.Card;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    System.out.println(cardOptions);
    System.out.println(cards);
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
    var camp = new Camp(6, List.of(campingTents, rainCollectors, vegetableGardens), List.of(), List.of());
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
