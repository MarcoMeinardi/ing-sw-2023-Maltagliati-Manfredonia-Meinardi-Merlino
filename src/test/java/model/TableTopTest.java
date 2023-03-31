package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Optional;

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
                        assertNotEquals(table.getCard(y, x), Optional.empty());
                    }
                }
            }
        }

        @Test
        public void testNeedRefill() throws InvalidMoveException {
            TableTop table = new TableTop(4);
            assertFalse(table.needRefill());
            for(int y = 0; y < 9; y++) {
                for(int x = 0; x < 9; x++) {
                    if(
						table.getCard(y, x).isPresent() &&
						(y != 8 || x != 4) &&
						(y != 3 || x != 8) &&
						(y != 4 || x != 0) &&
						(y != 4 || x != 3) &&
						(y != 5 || x != 4) &&
						(y != 5 || x != 3) // Will be removed later this last
					){
                        table.pickCard(y, x);
                    }
                }
            }
            assertFalse(table.needRefill());
			table.pickCard(5, 3);
            assertTrue(table.needRefill());
        }

        @Test
        public void testSetCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            assertEquals(table.getCard(0, 3), Optional.of(Card.Gatto));
        }

        @Test
        public void testIsPickable() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            assertTrue(table.isPickable(0, 3));
            assertFalse(table.isPickable(4, 3));
        }

        @Test
        public void testPickCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.pickCard(0, 3);
            assertEquals(table.getCard(0, 3), Optional.empty());
        }

        @Test
        public void testGetCard() throws InvalidMoveException {
            TableTop table = new TableTop(3);
            table.setCard(0, 3, Card.Gatto);
            assertEquals(table.getCard(0, 3), Optional.of(Card.Gatto));
        }

}
