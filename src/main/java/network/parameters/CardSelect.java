package network.parameters;

import model.Point;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * CardSelect class is used to send the selected cards from the client to the server.
 * @param column column of the shelf where the cards will be placed
 * @param selectedCards list of the selected cards
 * @author Ludovico
 */
public record CardSelect(int column, ArrayList<Point> selectedCards) implements Serializable {
}
