package view.cli;

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

	InGameOptions() {
		this.needHost = false;
		this.needTurn = false;
	}
	InGameOptions(boolean needTurn) {
		this.needHost = false;
		this.needTurn = needTurn;
	}
	InGameOptions(boolean needHost, boolean needTurn) {
		this.needHost = needHost;
		this.needTurn = needTurn;
	}

	public boolean needHost() {
		return needHost;
	}
	public boolean needTurn() {
		return needTurn;
	}
}
