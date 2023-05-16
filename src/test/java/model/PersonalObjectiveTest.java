package model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Unit test for PersonalObjective class.
 */

public class PersonalObjectiveTest {

    /**
     * Test for the constructor. Checks that the cells are correctly
     * initialized and that the name is correctly setted.
     *
     * @author Ludovico
     */
    @Test
    public void testConstructor(){

        PersonalObjective objective = new PersonalObjective("test", new Cell[]{
                new Cell(5, 0, Card.Pianta),
                new Cell(5, 1, Card.Gatto),
                new Cell(4, 4, Card.Gatto),
                new Cell(3, 3, Card.Libro),
                new Cell(2, 1, Card.Gioco),
                new Cell(0, 2, Card.Trofeo)
        });

        assertEquals(objective.getCellsCheck(), new Cell[]{
                new Cell(5, 0, Card.Pianta),
                new Cell(5, 1, Card.Gatto),
                new Cell(4, 4, Card.Gatto),
                new Cell(3, 3, Card.Libro),
                new Cell(2, 1, Card.Gioco),
                new Cell(0, 2, Card.Trofeo)
        });

        assertEquals(objective.getName(), "test");

    }

    /**
     * Test for the IsCompleted() method. Checks if optional of cockade
     * id empty first; checks that, in all the cases of possible acquired points,
     * that the method returns the optional with right points.
     * @author Ludovico
     */

    @Test
    public void testIsCompleted() {

        Optional<Cockade> cockade;
        PersonalObjective[] toTestObjectives  = PersonalObjective.generatePersonalObjectives(4);

        for(PersonalObjective objective : toTestObjectives) {

            cockade = objective.isCompleted(new Shelf());
            assertFalse(cockade.isPresent());

        }

        for(PersonalObjective objective : toTestObjectives){

            Shelf shelf = new Shelf();
            Cell[] cells = objective.getCellsCheck();
            for(Cell cell : cells){
                shelf.insertTest(cell.x(), cell.y(), cell.card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 12);

            shelf = new Shelf();
            for(int i = 0; i < cells.length-1; i++){
                shelf.insertTest(cells[i].x(), cells[i].y(), cells[i].card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 9);

            shelf = new Shelf();
            for(int i = 0; i < cells.length-2; i++){
                shelf.insertTest(cells[i].x(), cells[i].y(), cells[i].card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 6);

            shelf = new Shelf();
            for(int i = 0; i < cells.length-3; i++){
                shelf.insertTest(cells[i].x(), cells[i].y(), cells[i].card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 4);

            shelf = new Shelf();
            for(int i = 0; i < cells.length-4; i++){
                shelf.insertTest(cells[i].x(), cells[i].y(), cells[i].card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 2);

            shelf = new Shelf();
            for(int i = 0; i < cells.length-5; i++){
                shelf.insertTest(cells[i].x(), cells[i].y(), cells[i].card());
            }
            cockade = objective.isCompleted(shelf);
            assertTrue(cockade.isPresent());
            assertEquals(cockade.get().points(), 1);

        }

    }

}
