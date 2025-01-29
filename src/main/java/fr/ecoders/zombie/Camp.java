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

  public int availableSpace() {
    return buildings.size() - maxBuildCount;
  }

  public boolean isSpaceAvailable() {
    return buildings.size() < maxBuildCount;
  }

  public ResourceBank production() {
    var searchCost = Stream.of(SEARCH_COST.multiply(-searches().size()));
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
    return new Camp(maxBuildCount, buildings, searches);
  }

  Camp search(Searchable searchable) {
    if (!production().containsAll(SEARCH_COST)) {
      throw new IllegalArgumentException("Not enough materials to search " + searchable);
    }
    var searches = new ArrayList<>(this.searches);
    searches.add(searchable);
    return new Camp(maxBuildCount, buildings, searches);
  }
}
