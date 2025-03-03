package fr.ecoders.zombie.card;

public sealed interface Card permits Building, Zombie, Upgrade {
  String name();

  default String type() {
    return this.getClass()
      .getSimpleName();
  }
}
