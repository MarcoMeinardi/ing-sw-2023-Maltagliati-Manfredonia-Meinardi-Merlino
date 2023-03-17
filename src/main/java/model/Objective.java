package model;

import java.util.Optional;

public abstract class Objective {
    private final String name;

    protected Objective(String name) {
        this.name = name;
    }

    public abstract Optional<Trophy> isCompleted(Shelf shelf);

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Objective)) {
            return false;
        }
        return name.equals(((Objective)obj).getName());
    }
}
