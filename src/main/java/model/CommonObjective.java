package model;

import java.util.*;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CommonObjective extends Objective {
    String name;
    int value;
    int pointDecrement;
    Function<Shelf, Boolean> checkCompleted;

    private static final int INITIAL_VALUE = 8;
    private static final int POINT_DECREMENT = 2;
    private static final int POINT_DECREMENT_2_PLAYERS = 4;


    public static CommonObjective[] generateCommonObjectives(int nPlayers){
        CommonObjective[] selected_objectives = new CommonObjective[2];
        List<CommonObjective> all_objectives = new ArrayList();
        all_objectives.add(new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));
        Collections.shuffle(all_objectives);
        selected_objectives[0] = all_objectives.get(0);
        selected_objectives[1] = all_objectives.get(1);
        return selected_objectives;
    }

    /**
     * Constructor that creates a new common objective with a specified name,
     * value, and point decrement based on the number of players.
     * The objective also includes a function for checking if it has been completed.
     *
     * @param name           The name of the objective
     * @param nPlayers       The number of players in the game
     * @param checkCompleted A function for checking if the objective has been completed
     * @author Marco, Lorenzo, Ludovico, Riccardo
     */
    public CommonObjective(String name, int nPlayers, Function<Shelf, Boolean> checkCompleted) {
        super(name);
        this.name = name;
        value = INITIAL_VALUE;
        pointDecrement = nPlayers > 2 ? POINT_DECREMENT_2_PLAYERS : POINT_DECREMENT;
        this.checkCompleted = checkCompleted;
    }

    /**
     * Method that checks if the common objective has been completed using a specified
     * shelf, and returns an Optional object containing a Cockade trophy if it has.
     * If the objective is completed, the value of the trophy is decreased by the point decrement.
     *
     * @param shelf The shelf to check for completion of the objective
     * @return Optional object containing a Cockade trophy,
     * or an empty Optional object if the objective has not been completed.
     * @author Marco, Ludovico, Lorenzo, Riccardo
     */
    @Override
    public Optional<Cockade> isCompleted(Shelf shelf) {
        Optional<Cockade> trophy = Optional.empty();
        if (checkCompleted.apply(shelf)) {
            trophy = Optional.of(new Cockade(name, value));
            value -= pointDecrement;
        }
        return trophy;
    }

    enum Direction {
        Up, Down, Left, Right;

        public Point move_point(Point p) {
            switch (this) {
                case Up:
                    return new Point(p.x(), p.y() + 1);
                case Down:
                    return new Point(p.x(), p.y() - 1);
                case Left:
                    return new Point(p.x() - 1, p.y());
                case Right:
                    return new Point(p.x() + 1, p.y());
                default:
                    return null;
            }
        }
    }

    private static Boolean fourGroupsOfFourCards_checkCompatible(Point[] a, Point[] b) {
        for (Point p : a) {
            for (Point q : b) {
                if (p.x() == q.x() && p.y() == q.y()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void fourGroupsOfFourCards_generateGroup(Shelf shelf, ArrayList<Point[]> groups, ArrayList<Point> points, Point current_point, int depth, Direction last_direction) {
        points.add(current_point);
        if (depth == 4) {
            if(groups.stream().noneMatch(g -> points.containsAll(List.of(g)))) {
                Point[] group = new Point[4];
                for (int i = 0; i < 4; i++) {
                    group[i] = points.get(i);
                }
                groups.add(group);
            }
        } else {
            for (Direction direction : Direction.values()) {
                if (direction != last_direction) {
                    try {
                        Point next_point = direction.move_point(current_point);
                        Optional<Card> card = shelf.getCard(current_point.y(), current_point.x());
                        Optional<Card> next_card = shelf.getCard(next_point.y(), next_point.x());
                        if (card.isEmpty() || next_card.isEmpty()) {
                            continue;
                        }
                        if (card.get() != next_card.get()) {
                            continue;
                        }
                        if (points.contains(next_point)) {
                            continue;
                        }
                        fourGroupsOfFourCards_generateGroup(shelf, groups, points, next_point, depth + 1, direction);
                    } catch (InvalidMoveException e) {
                        continue;
                    }
                }
            }
        }
        points.remove(points.size() - 1);
    }

    private static Boolean fourGroupsOfFourCards(Shelf shelf) {
        ArrayList<Point[]> groups = new ArrayList<Point[]>();
        for (int y = 0; y < Shelf.COLUMNS; y++) {
            for (int x = 0; x < Shelf.ROWS; x++) {
                fourGroupsOfFourCards_generateGroup(shelf, groups, new ArrayList<Point>(), new Point(x, y), 1, null);
            }
        }
        int count;
        for (int i = 0; i < groups.size(); i++) {
            count = 0;
            for (int j = i + 1; j < groups.size(); j++) {
                if(fourGroupsOfFourCards_checkCompatible(groups.get(i), groups.get(j))) {
                    count++;
                }
            }
            if(count >= 3) {
                return true;
            }
        }
        return false;
    }
}
