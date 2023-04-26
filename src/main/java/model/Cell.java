package model;

import java.io.Serializable;

public record Cell(int y, int x, Card card) implements Serializable {}
