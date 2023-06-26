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

	InLobbyOptions() {
		this.needHost = false;
	}
	InLobbyOptions(boolean needHost) {
		this.needHost = needHost;
	}

	public boolean needHost() {
		return needHost;
	}
	public boolean needTurn() {
		return needTurn;
	}
}
