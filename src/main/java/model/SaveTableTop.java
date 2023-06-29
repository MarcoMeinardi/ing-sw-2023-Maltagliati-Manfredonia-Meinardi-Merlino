package model;

import java.io.Serializable;

/**
 * Copy of the tabletop as a serializable record
 * @param grid the current grid as a matrix of cards
 * @param deck the deck of cards
 */
public record SaveTableTop(
	Card[][] grid,
	CardsDeck deck
) implements Serializable {}
