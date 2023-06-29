package model;


/**
 * Abstract class that represents an objective
 */
public abstract class Objective {
    protected final String name;

    /**
     * Abstract constructor
     * Save only the name of the objective
     * @param name the name of the objective
     */
    public Objective(String name) {
        this.name = name;
    }

    /**
     * Getter for the `name` field
     * @return the `name` filed
     */
    public String getName() {
        return name;
    }

    /**
     * Override of the `equals` method
     * @param obj the objective to compare with
     * @return if the two objectives have the same name
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Objective)) {
            return false;
        }
        return name.equals(((Objective)obj).getName());
    }
}
