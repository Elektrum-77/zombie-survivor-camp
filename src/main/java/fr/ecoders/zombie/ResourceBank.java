package fr.ecoders.zombie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import java.util.stream.Stream;

public record ResourceBank(Map<Resource, Integer> resources) {
  public ResourceBank {
    resources = Map.copyOf(resources);
  }

  public static ResourceBank sumAll(Stream<ResourceBank> banks) {
    Objects.requireNonNull(banks);
    var resources = banks.filter(Objects::nonNull)
      .map(b -> b.resources.entrySet())
      .flatMap(Collection::stream)
      .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));
    return new ResourceBank(resources);
  }

  public ResourceBank add(Resource resource, int amount) {
    Objects.requireNonNull(resource);
    var resources = new HashMap<>(this.resources);
    resources.merge(resource, amount, Integer::sum);
    return new ResourceBank(resources);
  }

  public ResourceBank addAll(ResourceBank other) {
    Objects.requireNonNull(other);
    var resources = new HashMap<>(this.resources);
    other.resources.forEach((r, a) -> resources.merge(r, a, Integer::sum));
    return new ResourceBank(resources);
  }

  public ResourceBank multiply(int multiplier) {
    var resources = new HashMap<>(this.resources);
    resources.replaceAll((_, v) -> v * multiplier);
    return new ResourceBank(resources);
  }

  public boolean contains(Resource resource, int amount) {
    Objects.requireNonNull(resource);
    return resources.getOrDefault(resource, 0) >= amount;
  }

  public boolean containsAll(ResourceBank other) {
    Objects.requireNonNull(other);
    return other.resources.entrySet()
      .stream()
      .allMatch(e -> contains(e.getKey(), e.getValue()));
  }

  public ResourceBank remove(Resource resource, int amount) {
    Objects.requireNonNull(resource);
    var resources = new HashMap<>(add(resource, -amount).resources);
    resources.values()
      .removeIf(v -> v == 0);
    return new ResourceBank(resources);
  }

  public ResourceBank removeAll(ResourceBank other) {
    Objects.requireNonNull(other);
    var resources = new HashMap<>(addAll(other.multiply(-1)).resources);
    resources.values()
      .removeIf(v -> v == 0);
    return new ResourceBank(resources);
  }

  public ResourceBank filter(BiPredicate<Resource, Integer> predicate) {
    Objects.requireNonNull(predicate);
    var resources = new HashMap<>(this.resources);
    resources.entrySet()
      .removeIf(e -> !predicate.test(e.getKey(), e.getValue()));
    return new ResourceBank(resources);
  }

  public ResourceBank filterNegative() {
    return filter((_, v) -> v < 0);
  }


  @Override
  public String toString() {
    return resources.toString();
  }
}
