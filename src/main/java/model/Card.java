package model;

public enum Card {
    Gatto,
    Libro,
    Gioco,
    Cornice,
    Trofeo,
    Pianta;

    public boolean equals(Card card) {
        return this.ordinal() == card.ordinal();
    }
}
