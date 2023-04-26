package network.rpc.parameters;

import model.Cell;
import model.Shelf;
import model.TableTop;

import java.io.Serializable;
import java.util.ArrayList;

public record Update(String idPlayer, TableTop tableTop, Shelf shelf) implements Serializable {
}