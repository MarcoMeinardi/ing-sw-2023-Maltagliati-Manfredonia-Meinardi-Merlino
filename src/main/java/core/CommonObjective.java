package core;

import java.util.Optional;

public class CommonObjective extends Objective{
    @Override
    public Optional<Integer> isCompleted(Shelf shelf) {
        return Optional.empty();
    }
}
