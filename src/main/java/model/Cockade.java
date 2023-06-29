package model;

import java.io.Serializable;

/**
 * Record that holds information about an awarded cockade: name and points
 * @param name the name of the award
 * @param points the number of points given by the award
 */
public record Cockade(String name, int points) implements Serializable {}
