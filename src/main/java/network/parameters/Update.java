package network.parameters;

import model.Card;
import model.Cockade;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Update class is used to send the updated game information from the server to the clients.
 * @param idPlayer id of the author of the update
 * @param tableTop current table top
 * @param shelf shelf of the player
 * @param nextPlayer next player of the turn
 * @param commonObjectives list of the common objectives
 * @param newCommonObjectivesScores list of the points of the common objectives
 * @author Ludovico
 */
public record Update(String idPlayer, Card[][] tableTop, Card[][] shelf, String nextPlayer, ArrayList<Cockade> commonObjectives, ArrayList<Integer> newCommonObjectivesScores) implements Serializable {
}
