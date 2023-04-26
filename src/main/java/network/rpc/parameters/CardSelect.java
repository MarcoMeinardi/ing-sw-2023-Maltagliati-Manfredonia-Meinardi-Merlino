package network.rpc.parameters;

import model.Point;

import java.io.Serializable;
import java.util.ArrayList;

public record CardSelect(int column, ArrayList<Point> selectedCards) implements Serializable {
}
