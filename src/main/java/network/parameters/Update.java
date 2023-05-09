package network.parameters;

import model.Card;
import model.Cockade;

import java.io.Serializable;
import java.util.ArrayList;

public record Update(String idPlayer, Card[][] tableTop, Card[][] shelf, String nextPlayer, ArrayList<Cockade> commonObjectives) implements Serializable {
}
