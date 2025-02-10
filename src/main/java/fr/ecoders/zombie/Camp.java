package fr.ecoders.zombie;

import fr.ecoders.zombie.Card.Buildable;
import fr.ecoders.zombie.Card.Searchable;
import static fr.ecoders.zombie.ResourceBank.Resource.PEOPLE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.stream.Stream;

public record Camp(
  int maxBuildCount,
  List<Buildable> buildings,
  List<Searchable> searches) {
  public static final ResourceBank SEARCH_COST = new ResourceBank(Map.of(PEOPLE, 1));

  public Camp withSearches(List<Searchable> searches) {
    return new Camp(maxBuildCount, buildings, List.copyOf(searches));
  }

  public Camp withBuildings(List<Buildable> buildings) {
    return new Camp(maxBuildCount, List.copyOf(buildings), searches);
  }

  public Camp withMaxBuildCount(int maxBuildCount) {
    if (maxBuildCount < 1) {
      throw new IllegalArgumentException("maxBuildCount must be greater than 0");
    }
    if (maxBuildCount < buildings.size()) {
      throw new IllegalStateException("maxBuildCount must be greater than the current count of buildings");
    }
    return new Camp(maxBuildCount, buildings, searches);
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

  public ResourceBank production() {
    var searchCost = Stream.of(searchCost().multiply(-searches().size()));
    var production = buildings.stream()
      .map(Buildable::production);
    var searchProduction = searches.stream()
      .map(Searchable::search);

    var streams = Stream.of(searchCost, searchProduction, production);
    return ResourceBank.sumAll(streams.flatMap(identity()));
  }

  Camp construct(Buildable buildable) {
    if (!production().containsAll(buildable.cost())) {
      throw new IllegalArgumentException("Not enough materials to construct " + buildable);
    }
    if (!isSpaceAvailable()) {
      throw new IllegalArgumentException("Too many buildings");
    }
    var buildings = new ArrayList<>(this.buildings);
    buildings.add(buildable);
    return withBuildings(buildings);
  }

  Camp search(Searchable searchable) {
    if (!production().containsAll(searchCost())) {
      throw new IllegalArgumentException("Not enough materials to search " + searchable);
    }
    var searches = new ArrayList<>(this.searches);
    searches.add(searchable);
    return new Camp(maxBuildCount, buildings, searches);
  }

}
