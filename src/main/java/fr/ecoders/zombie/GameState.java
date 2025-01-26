package fr.ecoders.zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record GameState(
  Map<String, Camp> camps,
  List<Card> hand,
  String currentPlayer
) {
  public GameState {
    camps = Map.copyOf(camps);
    hand = List.copyOf(hand);
    Objects.requireNonNull(currentPlayer);
    if (!camps.containsKey(currentPlayer)) {
      throw new IllegalArgumentException("Player " + currentPlayer + " does not exist");
    }
  }

  public Card playedCard(int index) {
    Objects.checkIndex(index, hand.size());
    return hand.get(index);
  }

  public List<Card> discardedCards(int index) {
    var discarded = new ArrayList<>(hand);
    discarded.remove(index);
    return List.copyOf(discarded);
  }

  public Camp camp() {
    return camps.get(currentPlayer);
  }
}
