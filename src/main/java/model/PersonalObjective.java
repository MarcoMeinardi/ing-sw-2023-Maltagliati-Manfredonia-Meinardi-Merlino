package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class PersonalObjective extends Objective {
    private final Cell[] cellsCheck;
    private static final int[] points = {1, 2, 4, 6, 9, 12};
    public static final String MARKER = "PERSONAL_OBJECTIVE";

    /**
     * Constructor that creates a new personal objective with a specified name and
     * array of cells to check for completion.
     *
     * @param name The name of the personal objective
     * @param cellsCheck An array of cells to check for completion of the objective
     * @author Marco, Ludovico, Lorenzo, Riccardo
     */
    public PersonalObjective(String name, Cell[] cellsCheck) {
        super(name);
        this.cellsCheck = cellsCheck;
    }

    public PersonalObjective(String name) {
        // super objective must be the first statement
        super(generateAllPersonalObjectives().stream().filter(objective -> objective.getName().equals(name)).findFirst().get().getName());

        for (PersonalObjective objective : generateAllPersonalObjectives()) {
            if (objective.getName().equals(name)) {
                this.cellsCheck = objective.cellsCheck;
                return;
            }
        }
        throw new RuntimeException("Objective not found");
    }

    /**
     * Returns the array of cells to check for completion of the objective.
     *
     * @return The array of cells to check for completion of the objectives
     * @author Marco
     */
    public Cell[] getCellsCheck() {
        return cellsCheck;
    }

    /*
        * checks if the personal objective is completed by the player
        * @author Marco, Ludovico, Lorenzo
        *
        * @param shelf The shelf of the player
        * @returns An optional containing the cockade if the objective is completed, an empty optional otherwise
        * @throws RuntimeException if the logic is broken
     */
    public Optional<Cockade> isCompleted(Shelf shelf) {
        int count = 0;

        try {
            for (Cell cell : cellsCheck) {
                Optional<Card> pos = shelf.getCard(cell.y(), cell.x());
                if (pos.isPresent() && pos.get().getType() == cell.card()) {
                    count++;
                }
            }
        } catch (InvalidMoveException e) {
            throw new RuntimeException("Broken common objective");
        }

        if (count > 0) {
            return Optional.of(new Cockade(this.name,points[count - 1]));
        }
        return Optional.empty();

    }

    /**
     * Generates an ArrayList with all the existing personal objectives.
     *
     * @return An array of personal objectives
     * @author Ludovico
     */

    public static ArrayList<PersonalObjective> generateAllPersonalObjectives() {
        ArrayList<PersonalObjective> allObjectives = new ArrayList<>();

        allObjectives.add(new PersonalObjective(MARKER + "1", new Cell[]{
                new Cell(5, 0, Card.Type.Plant),
                new Cell(5, 2, Card.Type.Frame),
                new Cell(4, 4, Card.Type.Cat),
                new Cell(3, 3, Card.Type.Book),
                new Cell(2, 1, Card.Type.Game),
                new Cell(0, 2, Card.Type.Trophy)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "2", new Cell[]{
                new Cell(4,1, Card.Type.Plant),
                new Cell(3,0, Card.Type.Cat),
                new Cell(3,2, Card.Type.Game),
                new Cell(2,4, Card.Type.Book),
                new Cell(1,3, Card.Type.Trophy),
                new Cell(0,4, Card.Type.Frame)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "3", new Cell[]{
                new Cell(4,0, Card.Type.Frame),
                new Cell(4,3, Card.Type.Game),
                new Cell(3,2, Card.Type.Plant),
                new Cell(2,1, Card.Type.Cat),
                new Cell(2,4, Card.Type.Trophy),
                new Cell(0,0, Card.Type.Book)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "4", new Cell[]{
                new Cell(5,4, Card.Type.Game),
                new Cell(3,0, Card.Type.Trophy),
                new Cell(3,2, Card.Type.Frame),
                new Cell(2,3, Card.Type.Plant),
                new Cell(1,2, Card.Type.Cat),
                new Cell(1,1, Card.Type.Book)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "5", new Cell[]{
                new Cell(4,1, Card.Type.Trophy),
                new Cell(2,1, Card.Type.Frame),
                new Cell(2,2, Card.Type.Book),
                new Cell(1,4, Card.Type.Plant),
                new Cell(0,0, Card.Type.Game),
                new Cell(0,3, Card.Type.Cat)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "6", new Cell[]{
                new Cell(5,2, Card.Type.Trophy),
                new Cell(5,4, Card.Type.Cat),
                new Cell(3,3, Card.Type.Book),
                new Cell(1,1, Card.Type.Game),
                new Cell(1,3, Card.Type.Frame),
                new Cell(0,0, Card.Type.Plant)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "7", new Cell[]{
                new Cell(5,0, Card.Type.Cat),
                new Cell(4,3, Card.Type.Frame),
                new Cell(3,1, Card.Type.Plant),
                new Cell(2,0, Card.Type.Trophy),
                new Cell(1,4, Card.Type.Game),
                new Cell(0,2, Card.Type.Book)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "8", new Cell[]{
                new Cell(5,4, Card.Type.Frame),
                new Cell(4,1, Card.Type.Cat),
                new Cell(3,2, Card.Type.Trophy),
                new Cell(2,0, Card.Type.Plant),
                new Cell(1,3, Card.Type.Book),
                new Cell(0,3, Card.Type.Game)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "9", new Cell[]{
                new Cell(5,2, Card.Type.Game),
                new Cell(3,2, Card.Type.Cat),
                new Cell(2,4, Card.Type.Book),
                new Cell(1,1, Card.Type.Trophy),
                new Cell(1,4, Card.Type.Plant),
                new Cell(0,0, Card.Type.Frame)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "10", new Cell[]{
                new Cell(5,4, Card.Type.Trophy),
                new Cell(4,1, Card.Type.Game),
                new Cell(3,0, Card.Type.Book),
                new Cell(2,3, Card.Type.Cat),
                new Cell(1,1, Card.Type.Frame),
                new Cell(0,3, Card.Type.Plant)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "11", new Cell[]{
                new Cell(5,2, Card.Type.Plant),
                new Cell(4,1, Card.Type.Book),
                new Cell(3,0, Card.Type.Game),
                new Cell(2,2, Card.Type.Frame),
                new Cell(1,4, Card.Type.Cat),
                new Cell(0,3, Card.Type.Trophy)
        }));

        allObjectives.add(new PersonalObjective(MARKER + "12", new Cell[]{
                new Cell(5,2, Card.Type.Book),
                new Cell(4,1, Card.Type.Plant),
                new Cell(3,2, Card.Type.Frame),
                new Cell(2,3, Card.Type.Trophy),
                new Cell(1,4, Card.Type.Game),
                new Cell(0,0, Card.Type.Cat)
        }));

        return allObjectives;
    }

    /**
     * Generates an array of personal objectives, one for each player, randomly selected from the list of all the existing personal objectives.
     * @author Ludovico
     * 
     * @param nPlayers The number of players in the game
     * @return An array of personal objectives
     */
    public static PersonalObjective[] generatePersonalObjectives(int nPlayers) {
        PersonalObjective[] selectedPersonalObjectives = new PersonalObjective[nPlayers];

        ArrayList<PersonalObjective> allObjectives = generateAllPersonalObjectives();
        Collections.shuffle(allObjectives);
        for (int i = 0; i < nPlayers; i++) {
            selectedPersonalObjectives[i] = allObjectives.get(i);
        }

        return selectedPersonalObjectives;

    }

}
