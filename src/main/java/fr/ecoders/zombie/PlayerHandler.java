package fr.ecoders.zombie;

@FunctionalInterface
public interface PlayerHandler {
  PlayerTurn buildTurn(PlayerTurn.Builder builder) throws InterruptedException;
}
