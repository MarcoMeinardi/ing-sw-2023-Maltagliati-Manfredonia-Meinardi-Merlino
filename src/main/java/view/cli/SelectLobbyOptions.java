package view.cli;

// To use the `askOption` method, we need to implement this interface
public enum SelectLobbyOptions implements OptionsInterface {
	CREATE_LOBBY,
	JOIN_LOBBY,
	LIST_LOBBIES,
	QUIT;

	private final boolean needHost = false;
	private final boolean needTurn = false;

	public boolean needHost() {
		return needHost;
	}
	public boolean needTurn() {
		return needTurn;
	}
}
