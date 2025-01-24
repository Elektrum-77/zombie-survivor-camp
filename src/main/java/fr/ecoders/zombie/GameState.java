package fr.ecoders.zombie;

import java.util.List;
import java.util.Map;

public record GameState(Map<String, Camp> camps, List<Card> hand) {
}
