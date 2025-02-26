package fr.ecoders.zombie.state;

import fr.ecoders.zombie.card.Card;
import java.util.List;
import java.util.Objects;

public record Player(
  Camp camp,
  List<Card> hand) {
  public Player {
    Objects.requireNonNull(camp);
    hand = List.copyOf(hand);
  }

  public Player withCamp(Camp camp) {
    return new Player(camp, hand); // implicit check
  }

  public Player withHand(List<Card> hand) {
    return new Player(camp, hand); // implicit check
  }
}
