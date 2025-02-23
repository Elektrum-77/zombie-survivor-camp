package fr.ecoders.zombie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import java.util.stream.Stream;

public record ResourceBank(Map<Resource, Integer> resources) {
  public ResourceBank {
    resources = validateResources(resources);
  }

  private static Map<Resource, Integer> validateResources(Map<Resource, Integer> resources) {
    resources = new HashMap<>(resources);
    resources.values()
      .removeIf(i -> i == 0);
    return Map.copyOf(resources);
  }

  public static ResourceBank sumAll(Stream<ResourceBank> banks) {
    Objects.requireNonNull(banks);
    var resources = banks.filter(Objects::nonNull)
      .map(b -> b.resources.entrySet())
      .flatMap(Collection::stream)
      .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));
    return new ResourceBank(resources);
  }

  public boolean isEmpty() {
    return resources.isEmpty();
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

  public int missing(Resource resource, int amount) {
    Objects.requireNonNull(resource);
    return Math.max(amount - resources.getOrDefault(resource, 0), 0);
  }

  public ResourceBank missingAll(ResourceBank other) {
    Objects.requireNonNull(other);
    var resources = new HashMap<>(this.resources);
    var filter = resources.keySet();
    resources.keySet()
      .removeIf(not(filter::contains));
    var missing = other.subtractAll(new ResourceBank(resources));
    return missing.subtractAll(missing.filterNegative()); // only positives
  }

  public ResourceBank subtract(Resource resource, int amount) {
    Objects.requireNonNull(resource);
    return add(resource, amount * -1);
  }

  public ResourceBank subtractAll(ResourceBank other) {
    Objects.requireNonNull(other);
    return addAll(other.multiply(-1));
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
