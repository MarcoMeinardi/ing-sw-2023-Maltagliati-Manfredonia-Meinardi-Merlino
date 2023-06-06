package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class PersonalObjective extends Objective {
    private final Cell[] cellsCheck;
    private static final int[] points = {1, 2, 4, 6, 9, 12};

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

        allObjectives.add(new PersonalObjective("First", new Cell[]{
                new Cell(5, 0, Card.Type.Pianta),
                new Cell(5, 2, Card.Type.Cornice),
                new Cell(4, 4, Card.Type.Gatto),
                new Cell(3, 3, Card.Type.Libro),
                new Cell(2, 1, Card.Type.Gioco),
                new Cell(0, 2, Card.Type.Trofeo)
        }));

        allObjectives.add(new PersonalObjective("Second", new Cell[]{
                new Cell(4,1, Card.Type.Pianta),
                new Cell(3,0, Card.Type.Gatto),
                new Cell(3,2, Card.Type.Gioco),
                new Cell(2,4, Card.Type.Libro),
                new Cell(1,3, Card.Type.Trofeo),
                new Cell(0,4, Card.Type.Cornice)
        }));

        allObjectives.add(new PersonalObjective("Third", new Cell[]{
                new Cell(4,0, Card.Type.Cornice),
                new Cell(4,3, Card.Type.Gioco),
                new Cell(3,2, Card.Type.Pianta),
                new Cell(2,1, Card.Type.Gatto),
                new Cell(2,4, Card.Type.Trofeo),
                new Cell(0,0, Card.Type.Libro)

        }));

        allObjectives.add(new PersonalObjective("Fourth", new Cell[]{
                new Cell(5,4, Card.Type.Gioco),
                new Cell(3,0, Card.Type.Trofeo),
                new Cell(3,2, Card.Type.Cornice),
                new Cell(2,3, Card.Type.Pianta),
                new Cell(1,2, Card.Type.Gatto),
                new Cell(1,1, Card.Type.Libro)
        }));

        allObjectives.add(new PersonalObjective("Fifth", new Cell[]{
                new Cell(4,1, Card.Type.Trofeo),
                new Cell(2,1, Card.Type.Cornice),
                new Cell(2,2, Card.Type.Libro),
                new Cell(1,4, Card.Type.Pianta),
                new Cell(0,0, Card.Type.Gioco),
                new Cell(0,3, Card.Type.Gatto)
        }));

        allObjectives.add(new PersonalObjective("Sixth", new Cell[]{
                new Cell(5,2, Card.Type.Trofeo),
                new Cell(5,4, Card.Type.Gatto),
                new Cell(3,3, Card.Type.Libro),
                new Cell(1,1, Card.Type.Gioco),
                new Cell(1,3, Card.Type.Cornice),
                new Cell(0,0, Card.Type.Pianta)
        }));

        allObjectives.add(new PersonalObjective("Seventh", new Cell[]{
                new Cell(5,0, Card.Type.Gatto),
                new Cell(4,3, Card.Type.Cornice),
                new Cell(3,1, Card.Type.Pianta),
                new Cell(2,0, Card.Type.Trofeo),
                new Cell(1,4, Card.Type.Gioco),
                new Cell(0,2, Card.Type.Libro)
        }));

        allObjectives.add(new PersonalObjective("Eight", new Cell[]{
                new Cell(5,4, Card.Type.Cornice),
                new Cell(4,1, Card.Type.Gatto),
                new Cell(3,2, Card.Type.Trofeo),
                new Cell(2,0, Card.Type.Pianta),
                new Cell(1,3, Card.Type.Libro),
                new Cell(0,3, Card.Type.Gioco)
        }));

        allObjectives.add(new PersonalObjective("Ninth", new Cell[]{
                new Cell(5,2, Card.Type.Gioco),
                new Cell(3,2, Card.Type.Gatto),
                new Cell(2,4, Card.Type.Libro),
                new Cell(1,1, Card.Type.Trofeo),
                new Cell(1,4, Card.Type.Pianta),
                new Cell(0,0, Card.Type.Cornice)
        }));

        allObjectives.add(new PersonalObjective("Tenth", new Cell[]{
                new Cell(5,4, Card.Type.Trofeo),
                new Cell(4,1, Card.Type.Gioco),
                new Cell(3,0, Card.Type.Libro),
                new Cell(2,3, Card.Type.Gatto),
                new Cell(1,1, Card.Type.Cornice),
                new Cell(0,3, Card.Type.Pianta)
        }));

        allObjectives.add(new PersonalObjective("Eleventh", new Cell[]{
                new Cell(5,2, Card.Type.Pianta),
                new Cell(4,1, Card.Type.Libro),
                new Cell(3,0, Card.Type.Gioco),
                new Cell(2,2, Card.Type.Cornice),
                new Cell(1,4, Card.Type.Gatto),
                new Cell(0,3, Card.Type.Trofeo)
        }));

        allObjectives.add(new PersonalObjective("Twelfth", new Cell[]{
                new Cell(5,2, Card.Type.Libro),
                new Cell(4,1, Card.Type.Pianta),
                new Cell(3,2, Card.Type.Cornice),
                new Cell(2,3, Card.Type.Trofeo),
                new Cell(1,4, Card.Type.Gioco),
                new Cell(0,0, Card.Type.Gatto)
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
