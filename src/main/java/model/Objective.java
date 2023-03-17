package model;

import java.util.Optional;

public abstract class Objective {
    public abstract Optional<Trophy> isCompleted(Shelf shelf);
}
