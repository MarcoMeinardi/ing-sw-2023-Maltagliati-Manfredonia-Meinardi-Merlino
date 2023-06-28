package model;

import java.io.Serializable;

/**
 * Record that holds information about a cell: coordinates and card type
 * This object is used to check personal objectives.
 * @param y the y coordinate
 * @param x the x coordinate
 * @param card the card type
 */
public record Cell(int y, int x, Card.Type card) implements Serializable {}
