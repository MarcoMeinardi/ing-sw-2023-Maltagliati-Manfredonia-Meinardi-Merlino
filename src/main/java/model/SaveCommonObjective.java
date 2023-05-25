package model;

import java.io.Serializable;

public record SaveCommonObjective(
	String name,
	int points
) implements Serializable {}
