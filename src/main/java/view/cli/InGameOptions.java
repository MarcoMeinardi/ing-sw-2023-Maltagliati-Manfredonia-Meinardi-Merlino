package view.cli;

/**
 * Options to ask what to do during the game
 *
 * @author Marco
 */
public enum InGameOptions implements OptionsInterface {
	SEND_MESSAGE,
	SHOW_YOUR_SHELF,
	SHOW_ALL_SHELVES,
	SHOW_TABLETOP,
	SHOW_PERSONAL_OBJECTIVE,
	SHOW_COMMON_OBJECTIVES,
	STOP_GAME(true, false),
	PICK_CARDS(true);

	private final boolean needHost;
	private final boolean needTurn;

	/**
	 * Base constructor
	 */
	InGameOptions() {
		this.needHost = false;
		this.needTurn = false;
	}

	/**
	 * Constructor with the needHost parameter
	 */
	InGameOptions(boolean needTurn) {
		this.needHost = false;
		this.needTurn = needTurn;
	}

	/**
	 * Constructor with the needTurn parameter
	 */
	InGameOptions(boolean needHost, boolean needTurn) {
		this.needHost = needHost;
		this.needTurn = needTurn;
	}

	/**
	 * If the action can be performed only by the host
	 */
	public boolean needHost() {
		return needHost;
	}

	/**
	 * If the action can be performed only during the player's turn
	 */
	public boolean needTurn() {
		return needTurn;
	}
}
