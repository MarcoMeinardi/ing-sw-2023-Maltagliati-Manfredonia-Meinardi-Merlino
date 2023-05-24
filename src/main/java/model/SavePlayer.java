package model;

import java.io.Serializable;
import java.util.ArrayList;

public record SavePlayer(
	String name,
	String personalObjective,
	Card[][] shelf,
	ArrayList<Cockade> cockades,
	int points
) implements Serializable {}
