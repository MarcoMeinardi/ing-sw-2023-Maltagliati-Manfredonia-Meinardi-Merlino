package model;

import java.util.Optional;

public abstract class Objective {
    protected final String name;

    public Objective(String name) {
        this.name = name;
    }

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
