package network.parameters;

import model.Card;

import java.io.Serializable;

public record Update(String idPlayer, Card[][] tableTop, Card[][] shelf, String nextPlayer) implements Serializable {
}
