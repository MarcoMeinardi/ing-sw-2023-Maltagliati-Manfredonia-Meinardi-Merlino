package model;

import java.util.ArrayList;

public record SaveState(
	SaveTableTop tabletop,
	ArrayList<SavePlayer> players,
	ArrayList<String> commonObjectives,
	PlayerIterator playerIterator
) {}
