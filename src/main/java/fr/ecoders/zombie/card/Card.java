package fr.ecoders.zombie.card;

import fr.ecoders.zombie.state.ResourceBank;

public sealed interface Card permits Building, Card.Searchable, Card.Zombie, Upgrade {

  sealed interface Searchable extends Card permits Building {
    ResourceBank search();
  }

  sealed interface Zombie extends Card permits ZombieEvent {
    int count();
  }

}
