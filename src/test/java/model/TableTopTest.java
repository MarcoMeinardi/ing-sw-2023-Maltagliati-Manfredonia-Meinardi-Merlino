package model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TableTopTest {

    private static final int SIZE = 9;

    private static final int[][] requiredPlayers = {
            {11537317, 11537317, 11537317, 3       , 4       , 11537317, 11537317, 11537317, 11537317},
            {11537317, 11537317, 11537317, 2       , 2       , 4       , 11537317, 11537317, 11537317},
            {11537317, 11537317, 3       , 2       , 2       , 2       , 3       , 11537317, 11537317},
            {11537317, 4       , 2       , 2       , 2       , 2       , 2       , 2       , 3       },
            {4       , 2       , 2       , 2       , 2       , 2       , 2       , 2       , 4       },
            {3       , 2       , 2       , 2       , 2       , 2       , 2       , 4       , 11537317},
            {11537317, 11537317, 3       , 2       , 2       , 2       , 3       , 11537317, 11537317},
            {11537317, 11537317, 11537317, 4       , 2       , 2       , 11537317, 11537317, 11537317},
            {11537317, 11537317, 11537317, 11537317, 4       , 3       , 11537317, 11537317, 11537317}
    };

        @Test
        public void testFillTable() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    if (requiredPlayers[y][x] <= 3) {
                        assertNotEquals(table.getCard(y,x), Card.Empty);
                    }
                }
            }
        }

        @Test
        public void testNeedRefill() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.fillTable();
            assertFalse(table.needRefill());
            for(int y = 0; y < 9; y++) {
                for(int x = 0; x < 9; x++) {
                    if(y != 8 && x != 5){
                        table.pickCard(y, x);
                    }
                }
            }
            assertEquals(table.needRefill(), true);
        }

        @Test
        public void testSetCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            assertEquals(table.getCard(0, 3), Card.Gatto);
        }

        @Test
        public void testIsPickable() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            assertEquals(table.isPickable(0, 3), true);
            table.fillTable();
            assertEquals(table.isPickable(4, 3), false);
        }

        @Test
        public void testPickCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            table.pickCard(0, 3);
            assertEquals(table.getCard(0, 3), Card.Empty);
        }

        @Test
        public void testGetCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            assertEquals(table.getCard(0, 3), Card.Gatto);
        }


}