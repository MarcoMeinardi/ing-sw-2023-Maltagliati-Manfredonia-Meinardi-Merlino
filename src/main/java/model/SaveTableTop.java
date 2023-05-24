package model;

import java.io.Serializable;

public record SaveTableTop(
	Card[][] grid,
	CardsDeck deck
) implements Serializable {}
