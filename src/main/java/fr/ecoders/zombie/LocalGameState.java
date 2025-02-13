package fr.ecoders.zombie;

import fr.ecoders.zombie.card.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record LocalGameState(
  Map<String, Camp> camps,
  List<Card> hand,
  String currentPlayer
) {
  public LocalGameState {
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

  public LocalGameState withCamp(Camp camp) {
    var camps = new HashMap<>(this.camps);
    camps.put(currentPlayer, camp);
    return new LocalGameState(camps, hand, currentPlayer);
  }

  public LocalGameState withUserCamp(String username, Camp camp) {
    var camps = new HashMap<>(this.camps);
    camps.put(username, camp);
    return new LocalGameState(camps, hand, currentPlayer);
  }

  public LocalGameState withHand(List<Card> hand) {
    return new LocalGameState(camps, hand, currentPlayer);
  }

  public Camp userCamp(String username) {
    Objects.requireNonNull(username);
    if (!camps.containsKey(username)) {
      throw new IllegalArgumentException("User " + username + " does not exist");
    }
    return camps.get(username);
  }

  public Card handCard(int index) {
    Objects.checkIndex(index, hand.size());
    return hand.get(index);
  }
}
