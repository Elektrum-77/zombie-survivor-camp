package fr.ecoders.zombie.card;

public sealed interface Card permits Building, Zombie, Upgrade {
  String name();
}
