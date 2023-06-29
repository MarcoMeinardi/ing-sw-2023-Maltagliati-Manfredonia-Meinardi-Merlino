package view.cli;

/**
 * Options to ask what to do in the lobby
 *
 * @author Marco
 */
public enum InLobbyOptions implements OptionsInterface {
	SEND_MESSAGE,
	LIST_PLAYERS,
	LEAVE_LOBBY,
	START_GAME(true),
	LOAD_GAME(true);

	private final boolean needHost;
	private final boolean needTurn = false;

	/**
	 * Base constructor
	 */
	InLobbyOptions() {
		this.needHost = false;
	}

	/**
	 * Constructor with the needHost parameter
	 */
	InLobbyOptions(boolean needHost) {
		this.needHost = needHost;
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
