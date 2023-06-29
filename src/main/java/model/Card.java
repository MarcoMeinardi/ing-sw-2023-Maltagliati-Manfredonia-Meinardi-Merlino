package model;

import java.io.Serializable;

/**
 * Immutable class to hold information about a card.
 * It contains the type of the card and the index of its image.
 */
public class Card implements Serializable {
	public enum Type {
		Cat,
		Book,
		Game,
		Frame,
		Trophy,
		Plant
	}

	private final int imageIndex;
	private final Type type;

	/**
	 * Constructor for a card given the type and the image index.
	 * @param type The type of the card
	 * @param imageIndex The cards' image index
	 */
	public Card(Type type, int imageIndex) {
		this.type = type;
		this.imageIndex = imageIndex;
	}

	/**
	 * Constructor for a card given only the type.
	 * This constructor should be used only where the image is not used, so in testing and in the CLI.
	 * @param type The type of the card
	 */
	public Card(Type type) {
		this.type = type;
		this.imageIndex = -1;
	}

	/**
	 * `equals` override for the `Card` class.
	 * Two cards are equal if they have the same type
	 * @param obj The object to compare to
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			return ((Card)obj).type == this.type;
		} else if (obj instanceof Type) {
			return (Type)obj == this.type;
		}
		return false;
	}

	/**
	 * Getter for the `imageIndex` field.
	 * @return The cards' image index
	 */
	public int getImageIndex() {
		return imageIndex;
	}

	/**
	 * Getter for the `type` field.
	 * @return The cards' type
	 */
	public Type getType() {
		return type;
	}
}
