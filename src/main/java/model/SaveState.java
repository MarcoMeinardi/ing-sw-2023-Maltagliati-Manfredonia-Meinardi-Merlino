package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copy of the game state as a serializable record
 * @param tabletop the current tabletop as a serializable record
 * @param players the current players as a list of serializable records
 * @param commonObjectives the common objectives as a list of serializable records
 * @param playerIteratorIndex the index of the current player
 */
public record SaveState(
	SaveTableTop tabletop,
	ArrayList<SavePlayer> players,
	ArrayList<SaveCommonObjective> commonObjectives,
	int playerIteratorIndex
) implements Serializable {}
