package model;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Copy of a common objective as a serializable record
 * @param name the name of the objective
 * @param points the current points of the objective
 * @param completedBy the players that already completed the objective
 */
public record SaveCommonObjective(
	String name,
	int points,
	HashSet<String> completedBy
) implements Serializable {}
