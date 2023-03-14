package core;

import java.util.Optional;

public abstract class Objective {
    public abstract Optional<Integer> isCompleted(Shelf shelf);
}
