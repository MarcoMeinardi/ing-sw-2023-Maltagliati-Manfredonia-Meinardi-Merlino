package model;

import java.util.ArrayList;

/**
 * Class that holds the information about a player.
 * It contains the name, the personal objective, the shelf and the cockades of the player.
 * To construct the object, you need the name and the personal objective,
 * or the saved object representing the player.
 */
public class Player {
	private final String name;
    private final PersonalObjective personalObjective;
    private final Shelf shelf;
	private final ArrayList<Cockade> cockades;

	private int points;

	/**
	 * Constructor that creates a new player with a specified name and personal objective.
	 * The player's shelf and trophies list are initialized.
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 *
	 * @param name The name of the player
	 * @param personalObjective The personal objective of the player
	 */
	public Player(String name, PersonalObjective personalObjective) {
		this.name = name;
		this.personalObjective = personalObjective;
		shelf = new Shelf();
		this.cockades = new ArrayList<>();
		points = 0;
	}

	/**
	 * Constructor that creates a new player from a `SavePlayer` object
	 * @param savePlayer the saved object representing the player
	 */
	public Player(SavePlayer savePlayer) {
		this.name = savePlayer.name();
		this.personalObjective = new PersonalObjective(savePlayer.personalObjective());
		this.shelf = new Shelf(savePlayer.shelf());
		this.cockades = savePlayer.cockades();
		this.points = savePlayer.points();
	}

	/**
	 * Getter for the `shelf` field
	 * @return the `shelf` field
	 */
    public Shelf getShelf() {
        return shelf;
    }


	/**
	 * Getter for the `cockades` field
	 * @return the `cockades` field
	 */
    public ArrayList<Cockade> getCockades() {
        return cockades;
    }

	/**
	 * Getter for the `name` field
	 * @return the `name` field
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for the `points` field
	 * @return the `points` field
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Add a cockades and its points to the player
	 * @param cockade the cockade to add
	 */
	public void addCockade(Cockade cockade) {
		cockades.add(cockade);
		points += cockade.points();
	}

	/**
	 * Getter for the `personalObjective` field
	 * @return the `personalObjective` field
	 */
	public PersonalObjective getPersonalObjective() {
		return personalObjective;
	}

	/**
	 * Get a serializable object representing the player state
	 * @return a serializable object representing the player state
	 */
	public SavePlayer getSavePlayer() {
		return new SavePlayer(
			name,
			personalObjective.getName(),
			shelf.getSerializable(),
			cockades,
			points
		);
	}

	/**
	 * Override for the `equals` method
	 * We only need to compare the names of the players, since they are unique
	 * @param obj the player to compare
	 * @return if the two players have the same name
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return ((Player)obj).getName().equals(name);
	}

}
