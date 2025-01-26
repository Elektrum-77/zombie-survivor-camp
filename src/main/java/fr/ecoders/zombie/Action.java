package fr.ecoders.zombie;

import fr.ecoders.zombie.Card.Buildable;
import fr.ecoders.zombie.Card.Searchable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public sealed interface Action {
  static Action withGuard(GameState gameState, Action action) {
    return switch (action) {
      case Construct(int index) -> Construct.withGuard(gameState, index);
      case Search(int index) -> Search.withGuard(gameState, index);
    };
  }

  private static Buildable asBuildable(Card card) {
    if (!(card instanceof Buildable buildable)) {
      throw new IllegalArgumentException("Selected card is not buildable");
    }
    return buildable;
  }

  private static Searchable asSearchable(Card card) {
    if (!(card instanceof Searchable searchable)) {
      throw new IllegalArgumentException("Selected card is not searchable");
    }
    return searchable;
  }

  private static void discardAfterPlaying(Game game, List<Card> hand, int index, Consumer<Card> play) {
    hand = new ArrayList<>(hand);
    var card = hand.remove(index);
    play.accept(card);
    game.discardAll(hand);
  }

  void play(Game game, List<Card> hand, String player);

  record Construct(int index) implements Action {
    public static Construct withGuard(GameState gameState, int index) {
      Objects.requireNonNull(gameState);
      var building = asBuildable(gameState.playedCard(index));
      var camp = gameState.camp();
      camp.construct(building);
      return new Construct(index);
    }

    @Override
    public void play(Game game, List<Card> hand, String player) {
      Objects.requireNonNull(game);
      List.copyOf(hand);
      Objects.requireNonNull(player);
      discardAfterPlaying(
        game, hand, index, card -> game.updateCamp(player, camp -> camp.construct(asBuildable(card))));
    }
  }

  record Search(int index) implements Action {
    public static Search withGuard(GameState gameState, int index) {
      Objects.requireNonNull(gameState);
      var searchable = asSearchable(gameState.playedCard(index));
      var camp = gameState.camp();
      camp.search(searchable);
      return new Search(index);
    }

    @Override
    public void play(Game game, List<Card> hand, String player) {
      Objects.requireNonNull(game);
      List.copyOf(hand);
      Objects.requireNonNull(player);
      discardAfterPlaying(
        game, hand, index, card -> game.updateCamp(player, camp -> camp.search(asSearchable(card))));
    }
  }
}
