package fr.ecoders.zombie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import java.util.stream.Stream;

public record ResourceBank(Map<Resource, Integer> resources) {
  public ResourceBank {
    resources = Map.copyOf(resources);
  }

  public static ResourceBank sumAll(Stream<ResourceBank> banks) {
    var resources = banks.filter(Objects::nonNull)
      .map(b -> b.resources.entrySet())
      .flatMap(Collection::stream)
      .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));
    return new ResourceBank(resources);
  }

  public ResourceBank add(Resource resource, int amount) {
    var resources = new HashMap<>(this.resources);
    resources.merge(resource, amount, Integer::sum);
    return new ResourceBank(resources);
  }

  public ResourceBank addAll(ResourceBank other) {
    var resources = new HashMap<>(this.resources);
    other.resources.forEach((r, a) -> resources.merge(r, a, Integer::sum));
    return new ResourceBank(resources);
  }

  public ResourceBank multiply(int multiplier) {
    var resources = new HashMap<>(this.resources);
    resources.replaceAll((k, v) -> v * multiplier);
    return new ResourceBank(resources);
  }

  public boolean contains(Resource resource, int amount) {
    return resources.getOrDefault(resource, 0) >= amount;
  }

  public boolean containsAll(ResourceBank other) {
    return other.resources.entrySet()
      .stream()
      .allMatch(e -> contains(e.getKey(), e.getValue()));
  }

  public ResourceBank remove(Resource resource, int amount) {
    var resources = new HashMap<>(add(resource, -amount).resources);
    resources.values()
      .removeIf(v -> v == 0);
    return new ResourceBank(resources);
  }

  public ResourceBank removeAll(ResourceBank other) {
    var resources = new HashMap<>(addAll(other.multiply(-1)).resources);
    resources.values()
      .removeIf(v -> v == 0);
    return new ResourceBank(resources);
  }


  public enum Resource {
    PEOPLE,
    WATER,
    FOOD,
    MEDICINES,
    MILITARY,
    MATERIALS,
    FUEL
  }

  @Override
  public String toString() {
    return resources.toString();
  }
}
