package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copy of a player as a serializable record
 * @param name the name of the player
 * @param personalObjective the name of the personal objective
 * @param shelf a serializable representation of the player's shelf
 * @param cockades the awarded player's cockades
 * @param points the current points of the player
 */
public record SavePlayer(
	String name,
	String personalObjective,
	Card[][] shelf,
	ArrayList<Cockade> cockades,
	int points
) implements Serializable {}
