package fr.ecoders.zombie;

import static fr.ecoders.zombie.Resource.PEOPLE;
import fr.ecoders.zombie.card.Card.Buildable;
import fr.ecoders.zombie.card.Card.Searchable;
import fr.ecoders.zombie.card.Card.Zombie;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.stream.Stream;

public record Camp(
  int maxBuildCount,
  List<Buildable> buildings,
  List<Searchable> searches,
  List<Zombie> zombies) {
  public static final ResourceBank SEARCH_COST = new ResourceBank(Map.of(PEOPLE, 1));


  static int validateMaxBuildCount(int maxBuildCount) {
    if (maxBuildCount <= 0) {
      throw new IllegalArgumentException("maxBuildCount must be greater than 0");
    }
    return maxBuildCount;
  }

  public Camp {
    buildings = List.copyOf(buildings);
    searches = List.copyOf(searches);
    zombies = List.copyOf(zombies);
    maxBuildCount = validateMaxBuildCount(maxBuildCount);
  }

  public Camp withSearches(List<Searchable> searches) {
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public Camp withBuildings(List<Buildable> buildings) {
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public Camp withMaxBuildCount(int maxBuildCount) {
    if (maxBuildCount < buildings.size()) {
      throw new IllegalStateException("maxBuildCount must be greater than the current count of buildings");
    }
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public Camp withZombies(List<Zombie> zombies) {
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public int availableSpace() {
    return buildings.size() - maxBuildCount;
  }

  public boolean isSpaceAvailable() {
    return buildings.size() < maxBuildCount;
  }

  public ResourceBank searchCost() {
    return SEARCH_COST;
  }

  private ResourceBank totalSearchCost() {
    return searchCost().multiply(-searches().size());
  }

  public ResourceBank production() {
    var totalSearchCost = Stream.of(totalSearchCost());
    var production = buildings.stream()
      .map(Buildable::production);
    var searchProduction = searches.stream()
      .map(Searchable::search);

    var streams = Stream.of(totalSearchCost, searchProduction, production);
    return ResourceBank.sumAll(streams.flatMap(identity()));
  }
}
