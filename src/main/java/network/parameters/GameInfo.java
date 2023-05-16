package network.parameters;

import java.io.Serializable;
import java.util.ArrayList;

import model.Card;

public record GameInfo(
	Card[][] tableTop,
	ArrayList<String> players,
	ArrayList<Card[][]> shelves,
	ArrayList<String> commonObjectives,
	ArrayList<Integer> commonObjectivesPoints,
	String personalObjective,
	String currentPlayer
) implements Serializable {
}