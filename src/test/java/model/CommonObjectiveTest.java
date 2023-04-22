package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

public class CommonObjectiveTest {


    @Test
    public void testGenerateCommonObjectives() {

        CommonObjective[] commonObjectives = CommonObjective.generateCommonObjectives(2);
        assertEquals(2, commonObjectives.length);

        commonObjectives = CommonObjective.generateCommonObjectives(3);
        assertEquals(2, commonObjectives.length);

        commonObjectives = CommonObjective.generateCommonObjectives(4);
        assertEquals(2, commonObjectives.length);

    }


}
