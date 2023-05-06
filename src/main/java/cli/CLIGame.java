package cli;

import java.util.ArrayList;
import java.util.Optional;

import model.Card;
import model.Shelf;
import model.TableTop;
import network.parameters.StartingInfo;

public class CLIGame {
	String me;

	int nPlayers;
	Optional<Card>[][] tableTop;
	ArrayList<String> players;
	ArrayList<Optional<Card>[][]> shelves;
	ArrayList<String> commonObjectives;
	String personalObjective;

	public CLIGame(StartingInfo data, String me) {
		this.me = me;
		this.players = data.players();
		this.commonObjectives = data.commonObjectives();
		this.personalObjective = data.personalObjective();
		nPlayers = this.players.size();

		tableTop = new Optional[TableTop.SIZE][TableTop.SIZE];
		for (int y = 0;  y < TableTop.SIZE; y++) {
			for (int x = 0; x < TableTop.SIZE; x++) {
				tableTop[y][x] = data.tableTop()[y][x] != null ?
					Optional.of(data.tableTop()[y][x]) :
					Optional.empty();
			}
		}

		shelves = new ArrayList<>();
		for (int i = 0; i < nPlayers; i++) {
			shelves.add(new Optional[Shelf.ROWS][Shelf.COLUMNS]);
			for (int y = 0;  y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					shelves.get(i)[y][x] = data.shelves().get(i)[y][x] != null ?
						Optional.of(data.shelves().get(i)[y][x]) :
						Optional.empty();
				}
			}
		}
	}

	private void printShelf(Optional<Card>[][] shelf) {
		System.out.println("┌─┬─┬─┬─┬─┐");
		for (int y = Shelf.ROWS - 1; y >= 0; y--) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				System.out.print("│");
				if (shelf[y][x].isPresent()) {
					System.out.print(cardToChar(shelf[y][x].get()));
				} else {
					System.out.print(" ");
				}
			}
			System.out.println("│");
			if (y == 0) {
				System.out.println("└─┴─┴─┴─┴─┘");
			} else {
				System.out.println("├─┼─┼─┼─┼─┤");
			}
		}
		for (int i = 0; i < Shelf.COLUMNS; i++) {
			System.out.format(" %d", i + 1);
		}
		System.out.println();
	}

	private String cardToChar(Card card) {
		switch (card) {
			case Gatto   -> { return "C"; }
			case Libro   -> { return "B"; }
			case Gioco   -> { return "G"; }
			case Cornice -> { return "F"; }
			case Trofeo  -> { return "T"; }
			case Pianta  -> { return "P"; }
			default -> throw new RuntimeException("Invalid card");
		}
	}

	public void printYourShelf() {
		Optional<Card>[][] shelf = null;
		for (int i = 0; i < nPlayers; i++) {
			if (players.get(i).equals(me)) {
				shelf = shelves.get(i);
				break;
			}
		}  // Where is the for else loop?

		printShelf(shelf);
	}

	public void printAllShelves() {
		for (int i = 0; i < nPlayers; i++) {
			System.out.println(players.get(i) + ":");
			printShelf(shelves.get(i));
			System.out.println();
		}
	}

	public void printTableTop() {
		System.out.println("  ┌─┬─┬─┬─┬─┬─┬─┬─┬─┐");

		for (int y = TableTop.SIZE - 1; y >= 0; y--) {
			System.out.format("%d ", y + 1);
			for (int x = 0; x < TableTop.SIZE; x++) {
				System.out.print("│");
				if (tableTop[y][x].isPresent()) {
					System.out.print(cardToChar(tableTop[y][x].get()));
				} else {
					System.out.print(" ");
				}
			}
			System.out.println("│");
			if (y == 0) {
				System.out.println("  └─┴─┴─┴─┴─┴─┴─┴─┴─┘");
			} else {
				System.out.println("  ├─┼─┼─┼─┼─┼─┼─┼─┼─┤");
			}
		}

		System.out.print("  ");
		for (int x = 0; x < TableTop.SIZE; x++) {
			System.out.format(" %c", 'a' + x);
		}
		System.out.println();
	}
}
