package fr.ecoders.zombie.server;

import fr.ecoders.zombie.Camp;
import fr.ecoders.zombie.Card;
import fr.ecoders.zombie.LocalGameState;
import fr.ecoders.zombie.server.PlayerCommand.Action;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record CardWithAction(
  Card card,
  List<Action> action) {
  public CardWithAction {
    Objects.requireNonNull(card);
    action = List.copyOf(action);
  }

  private static boolean isConstructible(Card card, Camp camp) {
    var production = camp.production();
    return camp.isSpaceAvailable()
      && card instanceof Card.Buildable buildable
      && production.containsAll(buildable.cost());
  }

  private static boolean isSearchable(Card card, Camp camp) {
    var production = camp.production();
    return camp.isSpaceAvailable()
      && card instanceof Card.Buildable buildable
      && production.containsAll(buildable.cost());
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
