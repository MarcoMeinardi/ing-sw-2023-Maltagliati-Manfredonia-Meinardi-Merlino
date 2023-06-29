package view.cli;

// To use the `askOption` method, we need to implement this interface if this enum doesn't use the interface utilities
/**
 * Options to ask what to do in the lobby selection
 *
 * @author Marco
 */
public enum SelectLobbyOptions implements OptionsInterface {
	CREATE_LOBBY,
	JOIN_LOBBY,
	LIST_LOBBIES,
	QUIT;

	private final boolean needHost = false;
	private final boolean needTurn = false;

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
