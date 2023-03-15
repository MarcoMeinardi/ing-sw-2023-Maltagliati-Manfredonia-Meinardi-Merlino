package core;

import java.util.Optional;

public class PersonalObjective extends Objective{
    @Override
    public Optional<Trophy> isCompleted(Shelf shelf) {
        return Optional.empty();
    }
}
