package model;

import java.io.Serializable;

public record Cell(int y, int x, Card.Type card) implements Serializable {}
