package fr.ecoders.zombie;

public interface PlayerHandler {
  PlayerTurn buildTurn(PlayerTurn.Builder builder) throws InterruptedException;

  void kick();
}
