package view.cli;

// To use the `askOption` method, we need to implement this interface
public enum ConnectionModeOptions implements OptionsInterface {
	SOCKET,
	RMI;
	private final boolean needHost = false;
	private final boolean needTurn = false;

	public boolean needHost() {
		return needHost;
	}
	public boolean needTurn() {
		return needTurn;
	}
}
