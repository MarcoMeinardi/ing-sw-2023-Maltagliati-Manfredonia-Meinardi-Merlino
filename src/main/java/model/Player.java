package model;

import java.util.ArrayList;

public class Player {
	private final String name;
    private final PersonalObjective personalObjective;
    private final Shelf shelf;
	private ArrayList<Cockade> cockades;

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

    public Shelf getShelf() {
        return shelf;
    }

    public ArrayList<Cockade> getCockades() {
        return cockades;
    }

	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		this.points += points;
	}

	public void addCockade(Cockade cockade) {
		cockades.add(cockade);
		addPoints(cockade.points());
	}

	public PersonalObjective getPersonalObjective() {
		return personalObjective;
	}
}
