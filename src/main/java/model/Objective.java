package model;

import java.util.Optional;

public abstract class Objective {
    protected final String name;

    public Objective(String name) {
        this.name = name;
    }

    public abstract Optional<Cockade> isCompleted(Shelf shelf);

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
