package cli;

import java.util.ArrayList;
import java.util.Optional;

import model.Card;
import model.Shelf;
import model.TableTop;
import network.parameters.StartingInfo;

public class CLIGame {
	int nPlayers;
	Optional<Card>[][] tableTop;
	ArrayList<String> players;
	ArrayList<Optional<Card>[][]> shelfs;
	ArrayList<String> commonObjectives;
	String personalObjective;

	public CLIGame(StartingInfo data) {
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

		shelfs = new ArrayList<>();
		for (int i = 0; i < nPlayers; i++) {
			shelfs.add(new Optional[Shelf.ROWS][Shelf.COLUMNS]);
			for (int y = 0;  y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					shelfs.get(i)[y][x] = data.shelfs().get(i)[y][x] != null ?
						Optional.of(data.shelfs().get(i)[y][x]) :
						Optional.empty();
				}
			}
		}
	}
}
