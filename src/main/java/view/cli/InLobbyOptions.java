package view.cli;

public enum InLobbyOptions implements OptionsInterface {
	START_GAME(true),
	LEAVE_LOBBY,
	LIST_PLAYERS,
	SEND_MESSAGE;

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
