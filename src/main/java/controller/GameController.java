package controller;
import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class GameController {

    private Game game;
    private boolean endGame;

    /** Constructor that creates a new game with the specified players.
    * @author Ludovico
    * @param playersNames The names of the players
    */
    private GameController(ArrayList<String> playersNames) {
        game = new Game(playersNames);
    }

    private boolean checkFullShelf(Player player){
        return player.getShelf().isFull();
    }

    private void setEndGame(Player player){
        if(checkFullShelf(player)){
            endGame = true;
        }
    }

    private boolean checkRefillTable(){
        return game.getTabletop().needRefill();
    }

    private void refillTable(){
        if (checkRefillTable()) {
            game.getTabletop().fillTable();
        }
    }

    private void addPersonalCockade(Player player){
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        helpCockadePersonal.ifPresent(player::addCockade);
    }

    private void addCommonCockade(Player player){
        for(CommonObjective objective : game.getCommonObjectives()) {
            Optional<Cockade> helpCockadeCommon = objective.isCompleted(player.getShelf());
            helpCockadeCommon.ifPresent(player::addCockade);
        }
    }

    private ArrayList<Player> finalRanks(){
        ArrayList<Player> players = game.getPlayers();

        for(Player player : players) {

            ArrayList<Cockade> helpGroupCockades = player.getShelf().getGroupsCockades();
            for(Cockade cockade : helpGroupCockades) {
                player.addCockade(cockade);
            }

        }

        players.sort((player1, player2) -> player2.getPoints() - player1.getPoints());
        return players;
    }

    private void doMove(Player player, ArrayList<Point> positions, int column) throws InvalidMoveException {
		if (positions.size() < 1 || positions.size() > 3) {
			throw new InvalidMoveException("Invalid number of picked cards");
		}

		// Check that all the cards are adjacent
		for (int i = 0; i < positions.size() - 1; i++) {
			for (int j = 1; j < positions.size(); j++) {
				int dist = positions.get(i).distance(positions.get(j));
				if (dist != 1 && dist != 2) {
					throw new InvalidMoveException("Cards are not pickable (not adjacient)");
				}
			}
		}

		// Check that all the cards are singularly pickable
		for (Point position : positions) {
			if (!game.getTabletop().isPickable(position.y(), position.x())) {
				throw new InvalidMoveException("One of the selected cards is not pickable");
			}
		}

		if (positions.size() == 3) {
			// The three points have to be colinear
			// To check that, we can calculate the area of the triangle they form
			// If the area is 0, the points are colinear
			Point p1 = positions.get(0);
			Point p2 = positions.get(1);
			Point p3 = positions.get(2);

			// Double triangle area derived from Gauss's area formula
			int area =
				p1.x() * (p2.y() - p3.y()) +
				p2.x() * (p3.y() - p1.y()) +
				p3.x() * (p1.y() - p2.y());
			if (area != 0) {
				throw new InvalidMoveException("Cards are not pickable (not colinear)");
			}
		}

		ArrayList<Card> cards = new ArrayList<Card>();
		for (Point position : positions) {
			cards.add(game.getTabletop().pickCard(position.y(), position.x()));
		}
		player.getShelf().insert(column, cards);
    }
}


