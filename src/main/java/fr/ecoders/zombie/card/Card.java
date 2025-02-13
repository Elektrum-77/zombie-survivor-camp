package fr.ecoders.zombie.card;

import fr.ecoders.zombie.ResourceBank;

public sealed interface Card {

  sealed interface Searchable extends Card permits Building {
    ResourceBank search();
  }

  sealed interface Buildable extends Card permits Building {
    ResourceBank cost();

    ResourceBank production();
  }

  sealed interface Zombie extends Card permits ZombieEvent {
    int count();
  }

}
