package cli;

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
import network.parameters.StartingInfo;
import network.parameters.Update;

public class CLIGame {
	String me;

	int nPlayers;
	Optional<Card>[][] tableTop;
	ArrayList<String> players;
	ArrayList<Shelf> shelves;
	Shelf myShelf;
	ArrayList<String> commonObjectives;
	PersonalObjective personalObjective;

	public CLIGame(StartingInfo data, String me) {
		this.me = me;
		this.players = data.players();
		this.commonObjectives = data.commonObjectives();
		this.nPlayers = this.players.size();

		String personalObjectiveName = data.personalObjective();
		ArrayList<PersonalObjective> allObjectives = PersonalObjective.generateAllPersonalObjectives();
		for (PersonalObjective objective : allObjectives) {
			if (objective.getName().equals(personalObjectiveName)) {
				this.personalObjective = objective;
				break;
			}
		}
		if (this.personalObjective == null) {
			throw new RuntimeException("Unknown personal objective found");
		}

		updateTableTop(data.tableTop());

		shelves = new ArrayList<>();
		for (int i = 0; i < nPlayers; i++) {
			shelves.add(convertShelf(data.shelves().get(i)));
			if (players.get(i).equals(me)) {
				myShelf = shelves.get(i);
			}
		}
	}

	public void update(Update update) {
		updateTableTop(update.tableTop());

		for (int i = 0; i < nPlayers; i++) {
			if (players.get(i).equals(update.idPlayer())) {
				shelves.set(i, convertShelf(update.shelf()));
				if (players.get(i).equals(me)) {
					myShelf = shelves.get(i);
				}
				break;
			}
		}
	}

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

	private Shelf convertShelf(Card[][] shelf) {
		Optional<Card>[][] optionalShelf = new Optional[Shelf.ROWS][Shelf.COLUMNS];

		for (int y = 0;  y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				optionalShelf[y][x] = shelf[y][x] != null ?
					Optional.of(shelf[y][x]) :
					Optional.empty();
			}
		}

		return new Shelf(optionalShelf);
	}

	private void printShelf(Shelf shelf) {
		System.out.println("┌─┬─┬─┬─┬─┐");
		for (int y = Shelf.ROWS - 1; y >= 0; y--) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				System.out.print("│");
				try {
					if (shelf.getCard(y, x).isPresent()) {
						System.out.print(cardToChar(shelf.getCard(y, x).get()));
					} else {
						System.out.print(" ");
					}
				} catch (InvalidMoveException e) {
					throw new RuntimeException("Shelf is broken");
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
		printShelf(myShelf);
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

	public void printPersonalObjective() {
		Optional<Card>[][] shelfLikePersonalObjective = new Optional[Shelf.ROWS][Shelf.COLUMNS];

		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				shelfLikePersonalObjective[y][x] = Optional.empty();
			}
		}
		for (Cell cell : personalObjective.getCellsCheck()) {
			shelfLikePersonalObjective[cell.y()][cell.x()] = Optional.of(cell.card());
		}
		printShelf(new Shelf(shelfLikePersonalObjective));
		
		Optional<Cockade> cockade = personalObjective.isCompleted(myShelf);
		if (cockade.isEmpty()) {
			System.out.println("You won't get any point for your personal objective");
		} else {
			System.out.format(
				"You will get %d %s for your personal objective%n",
				cockade.get().points(),
				cockade.get().points() == 1 ? "point" : "points"
			);
		}
	}

	public void printCommonObjectives() {
		for (int i = 0; i < CommonObjective.N_COMMON_OBJECTIVES; i++) {
			System.out.format("%d: %s%n", i + 1, commonObjectives.get(i));
		}
	}
}
