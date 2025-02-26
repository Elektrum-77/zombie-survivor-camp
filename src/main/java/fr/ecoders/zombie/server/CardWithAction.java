package fr.ecoders.zombie.server;

import fr.ecoders.zombie.card.Building;
import fr.ecoders.zombie.state.Camp;
import fr.ecoders.zombie.card.Card;
import fr.ecoders.zombie.state.LocalGameState;
import fr.ecoders.zombie.Action;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record CardWithAction(
  Card card,
  List<Action> actions) {
  public CardWithAction {
    Objects.requireNonNull(card);
    actions = List.copyOf(actions);
  }

  private static boolean isConstructible(Card card, Camp camp) {
    var production = camp.production();
    return camp.isSpaceAvailable()
      && card instanceof Building buildable
      && production.containsAll(buildable.cost());
  }

  private static boolean isSearchable(Card card, Camp camp) {
    var production = camp.production();
    var searchCost = camp.searchCost();
    return production.containsAll(searchCost)
      && card instanceof Card.Searchable;
  }

  private static CardWithAction of(Card card, int index, Camp camp) {
    var actions = Stream.<Action>of(
        isConstructible(card, camp) ? new Action.Construct(index) : null,
        isSearchable(card, camp) ? new Action.Search(index) : null
      )
      .filter(Objects::nonNull)
      .toList();
    return new CardWithAction(card, actions);
  }

  public static List<CardWithAction> handWithAction(LocalGameState state) {
    Objects.requireNonNull(state);
    var hand = state.hand();
    return IntStream.range(0, hand.size())
      .mapToObj(i -> of(hand.get(i), i, state.camp()))
      .toList();
  }
}
