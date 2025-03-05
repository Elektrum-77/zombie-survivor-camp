package fr.ecoders.zombie.card;

import java.util.Objects;

public sealed interface Card permits Building, Zombie, Upgrade {
  String name();

  static String validateName(String name) {
    Objects.requireNonNull(name);
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    return name;
  }
}
