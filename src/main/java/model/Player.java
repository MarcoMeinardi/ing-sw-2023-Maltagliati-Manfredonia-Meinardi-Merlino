package model;

import java.util.ArrayList;

public class Player {
	private final String name;
    private final PersonalObjective personalObjective;
    private final Shelf shelf;
	private ArrayList<Trophy> trophies;

	public Player(String name, PersonalObjective personalObjective) {
		this.name = name;
		this.personalObjective = personalObjective;
		shelf = new Shelf();
		this.trophies = new ArrayList<>();
}

    public Shelf getShelf() {
        return shelf;
    }

    public ArrayList<Trophy> getTrophies() {
        return trophies;
    }

	public String getName() {
		return name;
	}

	public PersonalObjective getPersonalObjective() {
		return personalObjective;
	}
}
