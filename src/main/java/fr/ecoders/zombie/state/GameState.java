package fr.ecoders.zombie.state;

import fr.ecoders.zombie.GameOption;
import fr.ecoders.zombie.card.Card;
import static fr.ecoders.zombie.state.ResourceBank.EMPTY;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public record GameState(
  GameOption option,
  Map<String, Player> players,
  List<Card> cards,
  List<Card> discards) {

  public GameState {
    Objects.requireNonNull(option);
    players = Map.copyOf(players);
    cards = List.copyOf(cards);
    discards = List.copyOf(discards);
  }

  private static List<Card> draw(ArrayList<Card> cards, ArrayList<Card> discards, int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount must be greater than or equal to zero");
    }
    if (amount == 0) {
      return List.of();
    }
    amount = Math.min(cards.size() + discards.size(), amount);
    var drawn = new ArrayList<Card>(amount);
    for (int i = 0; i < amount; i++) {
      var card = cards.removeLast();
      if (cards.isEmpty()) {
        Collections.shuffle(discards);
        cards.addAll(discards);
        discards.clear();
      }
      drawn.add(card);
    }
    return drawn;
  }

  public Player player(String username) {
    Objects.requireNonNull(username);
    if (!players.containsKey(username)) {
      throw new IllegalArgumentException("Player " + username + " not found");
    }
    return players.get(username);
  }

  public GameState withPlayer(String username, Player player) {
    Objects.requireNonNull(username);
    if (!players.containsKey(username)) {
      throw new IllegalArgumentException("Player " + username + " not found");
    }
    Objects.requireNonNull(player);
    var players = new HashMap<>(this.players);
    players.put(username, player);
    return new GameState(option, players, cards, discards);
  }

  public GameState withCards(List<Card> cards) {
    return new GameState(option, players, cards, discards);
  }

  public GameState withDiscards(List<Card> discards) {
    return new GameState(option, players, cards, discards);
  }

  public GameState startUp() {
    var players = new HashMap<>(this.players);
    var cards = new ArrayList<>(this.cards);
    players.replaceAll((_, player) -> {
      var hand = new ArrayList<>(player.hand());
      IntStream.range(0, option.baseDrawCount())
        .mapToObj(_ -> cards.removeLast())
        .forEach(hand::add);
      return player.withHand(hand);
    });
    return new GameState(option, players, cards, discards);
  }

  public GameState cleanUp() {
    var players = new HashMap<>(this.players);
    var cards = new ArrayList<>(this.cards);
    var discards = new ArrayList<>(this.discards);

    players.replaceAll((_, player) -> {
      var hand = new ArrayList<>(player.hand());
      var camp = player.camp();

      // draw card
      var amount = Math.max(option.baseDrawCount() - hand.size(), 0);
      hand.addAll(draw(cards, discards, amount));

      // reset bonus production
      camp = camp.withBonusProduction(EMPTY);

      return player.withHand(hand)
        .withCamp(camp);
    });
    return new GameState(option, players, cards, discards);
  }
}
