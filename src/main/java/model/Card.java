package model;

import java.io.Serializable;

public class Card implements Serializable {
	public enum Type {
		Cat,
		Book,
		Game,
		Frame,
		Trophy,
		Plant
	}

	private int imageIndex;
	private Type type;

	public Card(Type type, int imageIndex) {
		this.type = type;
		this.imageIndex = imageIndex;
	}

	public Card(Type type) {
		this.type = type;
		this.imageIndex = -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			return ((Card)obj).type == this.type;
		} else if (obj instanceof Type) {
			return (Type)obj == this.type;
		}
		return false;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public Type getType() {
		return type;
	}
}
