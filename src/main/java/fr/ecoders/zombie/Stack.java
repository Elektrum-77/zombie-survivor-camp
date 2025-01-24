package fr.ecoders.zombie;

import java.util.ArrayList;
import java.util.List;

final class Stack {
  private final ArrayList<Card> cards = new ArrayList<>();
  private final ArrayList<Card> discards = new ArrayList<>();

  Stack(List<? extends Card> cards) {
    this.cards.addAll(List.copyOf(cards));
  }

  public void discardAll(List<Card> cards) {
    discards.addAll(List.copyOf(cards));
  }

  public List<Card> draw(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("count must be greater than 0");
    }
    var draw = new ArrayList<Card>();
    var limit = Math.min(cards.size() + discards.size(), count);
    for (int i = 0; i < limit; i++) {
      if (cards.isEmpty()) {
        cards.addAll(discards);
      }
      draw.add(cards.removeLast());
    }
    return draw;
  }

  public boolean isEmpty() {
    return cards.isEmpty() && discards.isEmpty();
  }
}
