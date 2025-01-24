package fr.ecoders.zombie;

import java.util.HashMap;
import java.util.Map;

public record ResourceBank(Map<Resource, Integer> resources) {
  public ResourceBank {
    resources = Map.copyOf(resources);
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
