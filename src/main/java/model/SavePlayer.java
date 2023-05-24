package model;

import java.util.ArrayList;

public record SavePlayer(
	String name,
	String personalObjective,
	Cell[][] shelf,
	ArrayList<Cockade> cockades,
	int points
) {}
