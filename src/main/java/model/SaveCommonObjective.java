package model;

import java.io.Serializable;
import java.util.HashSet;

public record SaveCommonObjective(
	String name,
	int points,
	HashSet<String> completedBy
) implements Serializable {}
