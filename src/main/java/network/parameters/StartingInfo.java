package network.parameters;

import java.io.Serializable;
import java.util.ArrayList;

import model.Card;

public record StartingInfo(
	Card[][] tableTop,
	ArrayList<String> players,
	ArrayList<Card[][]> shelfs,
	ArrayList<String> commonObjectives,
	String personalObjective
) implements Serializable {
}
