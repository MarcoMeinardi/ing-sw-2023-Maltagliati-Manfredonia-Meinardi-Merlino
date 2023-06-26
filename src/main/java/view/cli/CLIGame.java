package view.cli;

import java.util.ArrayList;
import java.util.Optional;

import model.Card;
import model.Cell;
import model.Cockade;
import model.CommonObjective;
import model.InvalidMoveException;
import model.PersonalObjective;
import model.Shelf;
import model.TableTop;
import network.parameters.GameInfo;
import network.parameters.Update;

/**
 * Class that contains the game's information needed in the CLI
 *
 * @author Marco
 */
public class CLIGame {
	String me;

	int nPlayers;
	Optional<Card>[][] tableTop;
	ArrayList<String> players;
	ArrayList<Shelf> shelves;
	Shelf myShelf;
	ArrayList<String> commonObjectives;
	ArrayList<Integer> commonObjectivesPoints;
	PersonalObjective personalObjective;

	/**
	 * Constructor of the class
	 * @param data the game's information
	 * @param me the player's name
	 *
	 * @author Marco
	 */
	public CLIGame(GameInfo data, String me) {
		this.me = me;
		this.players = data.players();
		this.commonObjectives = data.commonObjectives();
		this.commonObjectivesPoints = data.commonObjectivesPoints();
		this.nPlayers = this.players.size();

		this.personalObjective = new PersonalObjective(data.personalObjective());

		updateTableTop(data.tableTop());

		shelves = new ArrayList<>();
		for (int i = 0; i < nPlayers; i++) {
			shelves.add(new Shelf(data.shelves().get(i)));
			if (players.get(i).equals(me)) {
				myShelf = shelves.get(i);
			}
		}
	}

	/**
	 * Update the game state
	 * @param update an `Update` object from the server
	 *
	 * @author Marco
	 */
	public void update(Update update) {
		updateTableTop(update.tableTop());

		for (int i = 0; i < nPlayers; i++) {
			if (players.get(i).equals(update.idPlayer())) {
				shelves.set(i, new Shelf(update.shelf()));
				if (players.get(i).equals(me)) {
					myShelf = shelves.get(i);
				}
				break;
			}
		}

		for (int i = 0; i < update.completedObjectives().size(); i++) {
			for (int j = 0; j < commonObjectives.size(); j++) {
				if (update.completedObjectives().get(i).name().equals(commonObjectives.get(j))) {
					commonObjectivesPoints.set(j, update.newCommonObjectivesScores().get(i));
					break;
				}
			}
		}
	}

	/**
	 * Update the tabletop state. Called only from `update` and constructor
	 * @param tableTop a `Card` matrix representing the new tabletop
	 *
	 * @author Marco
	 */
	private void updateTableTop(Card[][] tableTop) {
		this.tableTop = new Optional[TableTop.SIZE][TableTop.SIZE];

		for (int y = 0;  y < TableTop.SIZE; y++) {
			for (int x = 0; x < TableTop.SIZE; x++) {
				this.tableTop[y][x] = tableTop[y][x] != null ?
					Optional.of(tableTop[y][x]) :
					Optional.empty();
			}
		}
	}
	
	/**
	 * Get the number of players in the game
	 * @return the number of players in the game
	 *
	 * @author Marco
	 */
	public int getNumberOfPlayers() {
		return nPlayers;
	}

	/**
	 * Print an ascii-art representing the given shelf
	 * @param shelf a `Shelf` object containing the shelf to print
	 * @throws RuntimeException if the shelf contains a non existing card or the shelf object is broken
	 *
	 * @author Marco
	 */
	private void printShelf(Shelf shelf) {
		System.out.println();
		System.out.println("┌───┬───┬───┬───┬───┐");
		for (int y = Shelf.ROWS - 1; y >= 0; y--) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				System.out.print("│");
				try {
					if (shelf.getCard(y, x).isPresent()) {
						System.out.format(" %s ", cardToChar(shelf.getCard(y, x).get()));
					} else {
						System.out.print("   ");
					}
				} catch (InvalidMoveException e) {
					throw new RuntimeException("Shelf is broken");
				}
			}
			System.out.println("│");
			if (y == 0) {
				System.out.println("└───┴───┴───┴───┴───┘");
			} else {
				System.out.println("├───┼───┼───┼───┼───┤");
			}
		}
		for (int i = 0; i < Shelf.COLUMNS; i++) {
			System.out.format("  %d ", i + 1);
		}
		System.out.println();
	}

	/**
	 * Convert a `Card` object to a single char representing it
	 * @param card the card to convert
	 * @throws RuntimeException if the card doesn't exist
	 * @return a single char representing the card
	 *
	 * @author Marco
	 */
	private String cardToChar(Card card) {
		switch (card.getType()) {
			case Gatto   -> { return "C"; }
			case Libro   -> { return "B"; }
			case Gioco   -> { return "G"; }
			case Cornice -> { return "F"; }
			case Trofeo  -> { return "T"; }
			case Pianta  -> { return "P"; }
			default -> throw new RuntimeException("Invalid card");
		}
	}

	/**
	 * Print the player's shelf
	 *
	 * @author Marco
	 */
	public void printYourShelf() {
		printShelf(myShelf);
	}

	/**
	 * Print all the players' shelves
	 *
	 * @author Marco
	 */
	public void printAllShelves() {
		for (int i = 0; i < nPlayers; i++) {
			System.out.println(players.get(i) + ":");
			printShelf(shelves.get(i));
			System.out.println();
		}
	}

	/**
	 * Utility function to get the ascii-art corner for the tabletop at the given coordinates
	 * @param y the y coordinate
	 * @param x the x coordinate
	 * @return the ascii-art corner for the tabletop at the given coordinates
	 *
	 * @author Marco
	 */
	private String getCorner(int y, int x) {
		boolean here = y >= 0 && x < TableTop.SIZE && TableTop.PLAYER_NUMBER_MASK[y][x] <= nPlayers;
		boolean left = y >= 0 && x > 0 && TableTop.PLAYER_NUMBER_MASK[y][x - 1] <= nPlayers;
		boolean up = y < TableTop.SIZE - 1 && x < TableTop.SIZE && TableTop.PLAYER_NUMBER_MASK[y + 1][x] <= nPlayers;
		boolean upLeft = y < TableTop.SIZE - 1 && x > 0 && TableTop.PLAYER_NUMBER_MASK[y + 1][x - 1] <= nPlayers;

		if (here) {
			if (left) {
				if (up) {
					return "┼";
				} else {
					if (upLeft) {
						return "┼";
					} else {
						return "┬";
					}
				}
			} else {
				if (up) {
					if (upLeft) {
						return "┼";
					} else {
						return "├";
					}
				} else {
					return "┌";
				}
			}
		} else {
			if (left) {
				if (up) {
					return "┼";
				} else {
					if (upLeft) {
						return "┤";
					} else {
						return "┐";
					}
				}
			} else {
				if (up) {
					if (upLeft) {
						return "┴";
					} else {
						return "└";
					}
				} else {
					if (upLeft) {
						return "┘";
					} else {
						return " ";
					}
				}
			}
		}
	}

	/**
	 * Print an ascii-art representing the current tabletop
	 *
	 * @author Marco
	 */
	public void printTableTop() {
		int firstRow = nPlayers == 2 ? TableTop.SIZE - 2 : TableTop.SIZE - 1;
		int lastRow = nPlayers == 2 ? 1 : 0;
		int firstColumn = nPlayers == 2 ? 1 : 0;
		int lastColumn = nPlayers == 2 ? TableTop.SIZE - 2 : TableTop.SIZE - 1;
		int offset = nPlayers == 2 ? 1 : 0;

		System.out.println();
		for (int y = firstRow; y >= lastRow - 1; y--) {
			System.out.print("  ");
			for (int x = firstColumn; x <= lastColumn + 1; x++) {
				System.out.print(getCorner(y, x));
				if (x == lastColumn + 1) break;

				if ((x < TableTop.SIZE && y >= 0 && TableTop.PLAYER_NUMBER_MASK[y][x] <= nPlayers) || (y < TableTop.SIZE - 1 && TableTop.PLAYER_NUMBER_MASK[y + 1][x] <= nPlayers)) {
					System.out.print("───");
				} else {
					System.out.print("   ");
				}
			}
			System.out.println();
			if (y == lastRow - 1) break;

			System.out.format("%d ", y + 1 - offset);
			for (int x = firstColumn; x <= lastColumn; x++) {
				if (TableTop.PLAYER_NUMBER_MASK[y][x] <= nPlayers || (x > 0 && TableTop.PLAYER_NUMBER_MASK[y][x - 1] <= nPlayers)) {
					System.out.print("│");
					if (tableTop[y][x].isPresent()) {
						System.out.format(" %s ", cardToChar(tableTop[y][x].get()));
					} else {
						System.out.print("   ");
					}
				} else {
					System.out.print("    ");
				}
			}
			if (TableTop.PLAYER_NUMBER_MASK[y][lastColumn] <= nPlayers) {
				System.out.print("│");
			}
			System.out.println();
		}
		System.out.print(" ");
		for (int x = firstColumn; x <= lastColumn; x++) {
			System.out.format("   %c", 'a' + x - offset);
		}
		System.out.println();
	}

	/**
	 * Print all the player's personal objectives as shelves and the points
	 * that they would give at the current state
	 *
	 * @author Marco
	 */
	public void printPersonalObjective() {
		Optional<Card>[][] shelfLikePersonalObjective = new Optional[Shelf.ROWS][Shelf.COLUMNS];

		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				shelfLikePersonalObjective[y][x] = Optional.empty();
			}
		}
		for (Cell cell : personalObjective.getCellsCheck()) {
			shelfLikePersonalObjective[cell.y()][cell.x()] = Optional.of(new Card(cell.card()));
		}
		printShelf(new Shelf(shelfLikePersonalObjective));
		
		Optional<Cockade> cockade = personalObjective.isCompleted(myShelf);
		if (cockade.isEmpty()) {
			System.out.println("[*] You haven't completed any part of your personal objective yet");
		} else {
			System.out.format(
				"[*] You will get %d %s for your personal objective%n",
				cockade.get().points(),
				cockade.get().points() == 1 ? "point" : "points"
			);
		}
	}

	/**
	 * Print the common objectives and their current values
	 *
	 * @author Marco
	 */
	public void printCommonObjectives() {
		System.out.println("[*] Common objectives:");
		for (int i = 0; i < CommonObjective.N_COMMON_OBJECTIVES; i++) {
			System.out.format(" %d: %s ( %d points )%n", i + 1, commonObjectives.get(i), commonObjectivesPoints.get(i));
		}
	}
}
