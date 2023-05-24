package model;

import java.io.Serializable;
import java.util.ArrayList;

public record SaveState(
	SaveTableTop tabletop,
	ArrayList<SavePlayer> players,
	ArrayList<String> commonObjectives,
	PlayerIterator playerIterator
) implements Serializable {}
