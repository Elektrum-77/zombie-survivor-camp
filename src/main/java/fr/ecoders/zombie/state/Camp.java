package fr.ecoders.zombie.state;

import static fr.ecoders.zombie.Resource.PEOPLE;
import fr.ecoders.zombie.card.Card.Searchable;
import fr.ecoders.zombie.card.Card.Zombie;
import static fr.ecoders.zombie.state.ResourceBank.sumAll;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record Camp(
  int maxBuildCount,
  List<UpgradableBuilding> buildings,
  List<Searchable> searches,
  List<Zombie> zombies) {
  public static final ResourceBank SEARCH_COST = new ResourceBank(Map.of(PEOPLE, 1));


  public Camp {
    buildings = List.copyOf(buildings);
    searches = List.copyOf(searches);
    zombies = List.copyOf(zombies);
    maxBuildCount = validateMaxBuildCount(maxBuildCount, buildings.size());
  }

  static int validateMaxBuildCount(int maxBuildCount, int buildCount) {
    if (maxBuildCount <= 0 || maxBuildCount < buildCount) {
      throw new IllegalArgumentException("maxBuildCount must be greater than 0 and equal or greater than buildCount");
    }
    return maxBuildCount;
  }

  public Camp withSearches(List<Searchable> searches) {
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public Camp withBuildings(List<UpgradableBuilding> buildings) {
    return new Camp(maxBuildCount, buildings, searches, zombies);
  }

  public Camp withMaxBuildCount(int maxBuildCount) {
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

  private Stream<UpgradableBuilding> buildingStream() {
    return buildings.stream();
  }

  private Stream<Searchable> searchStream() {
    return searches.stream();
  }

  public ResourceBank production() {
    var totalSearchCost = totalSearchCost();
    var buildingProduction = sumAll(buildingStream().map(UpgradableBuilding::production));
    var searchProduction = sumAll(searchStream().map(Searchable::search));
    return sumAll(Stream.of(totalSearchCost, buildingProduction, searchProduction));
  }
}
