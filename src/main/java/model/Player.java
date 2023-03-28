package model;

import java.util.ArrayList;

public class Player {
	private final String name;
    private final PersonalObjective personalObjective;
    private final Shelf shelf;
	private ArrayList<Cockade> trophies;

	/**
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 * Constructor that creates a new player with a specified name and personal objective.
	 * The player's shelf and trophies list are initialized.
	 *
	 * @param name The name of the player
	 * @param personalObjective The personal objective of the player
	 */
	public Player(String name, PersonalObjective personalObjective) {
		this.name = name;
		this.personalObjective = personalObjective;
		shelf = new Shelf();
		this.trophies = new ArrayList<>();
	}

    public Shelf getShelf() {
        return shelf;
    }

    public ArrayList<Cockade> getTrophies() {
        return trophies;
    }

	public String getName() {
		return name;
	}

	public PersonalObjective getPersonalObjective() {
		return personalObjective;
	}
}
