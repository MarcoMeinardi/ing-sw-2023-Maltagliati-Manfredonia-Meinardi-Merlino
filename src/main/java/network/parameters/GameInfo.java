package network.parameters;

import java.io.Serializable;
import java.util.ArrayList;

import controller.lobby.Lobby;
import model.Card;

/**
 * GameInfo class is used to send the game information from the server to the clients.
 * @param lobby the current lobby
 * @param tableTop current tabletop
 * @param players list of the players
 * @param shelves list of the shelves of the players
 * @param commonObjectives list of the common objectives
 * @param commonObjectivesPoints list of the points of the common objectives
 * @param personalObjective personal objective of the player
 * @param currentPlayer current player of the turn
 * @author Marco
 */
public record GameInfo(
	Lobby lobby,
	Card[][] tableTop,
	ArrayList<String> players,
	ArrayList<Card[][]> shelves,
	ArrayList<String> commonObjectives,
	ArrayList<Integer> commonObjectivesPoints,
	String personalObjective,
	String currentPlayer
) implements Serializable {
}
