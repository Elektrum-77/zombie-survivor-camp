package fr.ecoders.zombie.state;

import fr.ecoders.zombie.card.Card;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record LocalGameState(
  List<String> playerOrder,
  Map<String, Camp> camps,
  List<Card> hand,
  List<Card> discards,
  String currentPlayer
) {
  public LocalGameState {
    playerOrder = List.copyOf(playerOrder);
    camps = Map.copyOf(camps);
    hand = List.copyOf(hand);
    discards = List.copyOf(discards);
    Objects.requireNonNull(currentPlayer);
    if (!camps.containsKey(currentPlayer)) {
      throw new IllegalArgumentException("Player " + currentPlayer + " does not exist");
    }
  }

  public Camp camp() {
    return camps.get(currentPlayer);
  }

}
