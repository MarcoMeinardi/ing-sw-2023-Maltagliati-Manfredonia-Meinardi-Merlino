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
     * @author Marco, Ludovico, Lorenzo, Riccardo
     *
     * @param name The name of the personal objective
     * @param cellsCheck An array of cells to check for completion of the objective
     */
    public PersonalObjective(String name, Cell[] cellsCheck) {
        super(name);
        this.cellsCheck = cellsCheck;
    }

    // TODO doc post invalidMoveException
    @Override
    public Optional<Cockade> isCompleted(Shelf shelf) {
        int count = 0;
        try {
            for (Cell cell : cellsCheck) {
                Optional<Card> pos = shelf.getCard(cell.y(), cell.x());
                if (pos.isPresent() && pos.get() == cell.card()) {
                    count++;
                }
            }
        } catch (InvalidMoveException e) {
            // TODO add log
        }
        if (count > 0) {
            return Optional.of(new Cockade(this.name,points[count - 1]));
        }
        return Optional.empty();
    }

    public static PersonalObjective[] generatePersonalObjectives(int nPlayers) {
        PersonalObjective[] selectedPersonalObjectives = new PersonalObjective[nPlayers];
        ArrayList<PersonalObjective> all_objectives = new ArrayList<>();

        Cell[] cellsCheckFirst = new Cell[6];
        Cell[] cellsCheckSecond = new Cell[6];
        Cell[] cellsCheckThird = new Cell[6];
        Cell[] cellsCheckFourth = new Cell[6];
        Cell[] cellsCheckFifth = new Cell[6];
        Cell[] cellsCheckSixth = new Cell[6];
        Cell[] cellsCheckSeventh = new Cell[6];
        Cell[] cellsCheckEighth = new Cell[6];
        Cell[] cellsCheckNinth = new Cell[6];
        Cell[] cellsCheckTenth = new Cell[6];
        Cell[] cellsCheckEleventh = new Cell[6];
        Cell[] cellsCheckTwelfth = new Cell[6];

        cellsCheckFirst[0] = new Cell(0,0, Card.Pianta);
        cellsCheckFirst[1] = new Cell(0,2, Card.Cornice);
        cellsCheckFirst[2] = new Cell(1,4, Card.Gatto);
        cellsCheckFirst[3] = new Cell(2,3, Card.Libro);
        cellsCheckFirst[4] = new Cell(3,1, Card.Gioco);
        cellsCheckFirst[5] = new Cell(5,2, Card.Trofeo);

        cellsCheckSecond[0] = new Cell(1,1, Card.Pianta);
        cellsCheckSecond[1] = new Cell(2,0, Card.Gatto);
        cellsCheckSecond[2] = new Cell(2,2, Card.Gioco);
        cellsCheckSecond[3] = new Cell(3,4, Card.Libro);
        cellsCheckSecond[4] = new Cell(4,3, Card.Trofeo);
        cellsCheckSecond[5] = new Cell(5,4, Card.Cornice);

        cellsCheckThird[0] = new Cell(1,0, Card.Cornice);
        cellsCheckThird[1] = new Cell(1,3, Card.Gioco);
        cellsCheckThird[2] = new Cell(2,2, Card.Pianta);
        cellsCheckThird[3] = new Cell(3,1, Card.Gatto);
        cellsCheckThird[4] = new Cell(3,4, Card.Trofeo);
        cellsCheckThird[5] = new Cell(5,0, Card.Libro);

        cellsCheckFourth[0] = new Cell(0,4, Card.Gioco);
        cellsCheckFourth[1] = new Cell(2,0, Card.Trofeo);
        cellsCheckFourth[2] = new Cell(2,2, Card.Cornice);
        cellsCheckFourth[3] = new Cell(3,3, Card.Pianta);
        cellsCheckFourth[4] = new Cell(4,2, Card.Gatto);
        cellsCheckFourth[5] = new Cell(4,1, Card.Libro);

        cellsCheckFifth[0] = new Cell(1,1, Card.Trofeo);
        cellsCheckFifth[1] = new Cell(3,1, Card.Cornice);
        cellsCheckFifth[2] = new Cell(3,2, Card.Libro);
        cellsCheckFifth[3] = new Cell(4,4, Card.Pianta);
        cellsCheckFifth[4] = new Cell(5,0, Card.Gioco);
        cellsCheckFifth[5] = new Cell(5,3, Card.Gatto);

        cellsCheckSixth[0] = new Cell(0,2, Card.Trofeo);
        cellsCheckSixth[1] = new Cell(0,4, Card.Gatto);
        cellsCheckSixth[2] = new Cell(2,3, Card.Libro);
        cellsCheckSixth[3] = new Cell(4,1, Card.Gioco);
        cellsCheckSixth[4] = new Cell(4,3, Card.Cornice);
        cellsCheckSixth[5] = new Cell(5,0, Card.Pianta);

        cellsCheckSeventh[0] = new Cell(0,0, Card.Gatto);
        cellsCheckSeventh[1] = new Cell(1,3, Card.Cornice);
        cellsCheckSeventh[2] = new Cell(2,1, Card.Pianta);
        cellsCheckSeventh[3] = new Cell(3,0, Card.Trofeo);
        cellsCheckSeventh[4] = new Cell(4,4, Card.Gioco);
        cellsCheckSeventh[5] = new Cell(5,2, Card.Libro);

        cellsCheckEighth[0] = new Cell(0,4, Card.Cornice);
        cellsCheckEighth[1] = new Cell(1,1, Card.Gatto);
        cellsCheckEighth[2] = new Cell(2,2, Card.Trofeo);
        cellsCheckEighth[3] = new Cell(3,0, Card.Pianta);
        cellsCheckEighth[4] = new Cell(4,3, Card.Libro);
        cellsCheckEighth[5] = new Cell(5,3, Card.Gioco);

        cellsCheckNinth[0] = new Cell(0,2, Card.Gioco);
        cellsCheckNinth[1] = new Cell(2,2, Card.Gatto);
        cellsCheckNinth[2] = new Cell(3,4, Card.Libro);
        cellsCheckNinth[3] = new Cell(4,1, Card.Trofeo);
        cellsCheckNinth[4] = new Cell(4,4, Card.Pianta);
        cellsCheckNinth[5] = new Cell(5,0, Card.Cornice);

        cellsCheckTenth[0] = new Cell(0,4, Card.Trofeo);
        cellsCheckTenth[1] = new Cell(1,1, Card.Gioco);
        cellsCheckTenth[2] = new Cell(2,0, Card.Libro);
        cellsCheckTenth[3] = new Cell(3,3, Card.Gatto);
        cellsCheckTenth[4] = new Cell(4,1, Card.Cornice);
        cellsCheckTenth[5] = new Cell(5,3, Card.Pianta);

        cellsCheckEleventh[0] = new Cell(0,2, Card.Pianta);
        cellsCheckEleventh[1] = new Cell(1,1, Card.Libro);
        cellsCheckEleventh[2] = new Cell(0,2, Card.Gioco);
        cellsCheckEleventh[3] = new Cell(3,2, Card.Cornice);
        cellsCheckEleventh[4] = new Cell(4,4, Card.Gatto);
        cellsCheckEleventh[5] = new Cell(5,3, Card.Trofeo);

        cellsCheckTwelfth[0] = new Cell(0,2, Card.Libro);
        cellsCheckTwelfth[1] = new Cell(1,1, Card.Pianta);
        cellsCheckTwelfth[2] = new Cell(2,2, Card.Cornice);
        cellsCheckTwelfth[3] = new Cell(3,3, Card.Trofeo);
        cellsCheckTwelfth[4] = new Cell(4,4, Card.Gioco);
        cellsCheckTwelfth[5] = new Cell(5,0, Card.Gatto);

        all_objectives.add(new PersonalObjective("First", cellsCheckFirst));
        all_objectives.add(new PersonalObjective("Second", cellsCheckSecond));
        all_objectives.add(new PersonalObjective("Third", cellsCheckThird));
        all_objectives.add(new PersonalObjective("Fourth", cellsCheckFourth));
        all_objectives.add(new PersonalObjective("Fifth", cellsCheckFifth));
        all_objectives.add(new PersonalObjective("Sixth", cellsCheckSixth));
        all_objectives.add(new PersonalObjective("Seventh", cellsCheckSeventh));
        all_objectives.add(new PersonalObjective("Eighth", cellsCheckEighth));
        all_objectives.add(new PersonalObjective("Ninth", cellsCheckNinth));
        all_objectives.add(new PersonalObjective("Tenth", cellsCheckTenth));
        all_objectives.add(new PersonalObjective("Eleventh", cellsCheckEleventh));
        all_objectives.add(new PersonalObjective("Twelfth", cellsCheckTwelfth));

        Collections.shuffle(all_objectives);
        selectedPersonalObjectives[0] = all_objectives.get(0);
        selectedPersonalObjectives[1] = all_objectives.get(1);
        selectedPersonalObjectives[2] = all_objectives.get(2);
        selectedPersonalObjectives[3] = all_objectives.get(3);
        return selectedPersonalObjectives;
    }

}
