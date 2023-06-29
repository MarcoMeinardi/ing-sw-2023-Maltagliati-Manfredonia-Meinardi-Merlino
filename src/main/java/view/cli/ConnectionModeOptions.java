package view.cli;

// To use the `askOption` method, we need to implement this interface if this enum doesn't use the interface utilities
/**
 * Options to ask the connection method
 *
 * @author Lorenzo
 */
public enum ConnectionModeOptions implements OptionsInterface {
	SOCKET,
	RMI;
	private final boolean needHost = false;
	private final boolean needTurn = false;

    /**
     * If the action can be performed only by the host
     */
	public boolean needHost() {
		return needHost;
	}

    /**
     * If the action can be performed only during a player's turn
     */
	public boolean needTurn() {
		return needTurn;
	}
}
