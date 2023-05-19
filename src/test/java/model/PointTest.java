package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PointTest {

    @Test
    public void testPoint() {
		Point p1 = new Point(1, 2);
		Point p2 = new Point(3, 5);
		assertEquals(p1.y(), 1);
		assertEquals(p2.x(), 5);
		assertEquals(p1.distance(p2), 5);
    }
}
