package view.cli;

public enum InGameOptions implements OptionsInterface {
	SHOW_YOUR_SHELF,
	SHOW_ALL_SHELVES,
	SHOW_TABLETOP,
	SHOW_PERSONAL_OBJECTIVE,
	SHOW_COMMON_OBJECTIVES,
	SEND_MESSAGE,
	PICK_CARDS(true);

	private final boolean needHost = false;
	private final boolean needTurn;

	InGameOptions() {
		this.needTurn = false;
	}
	InGameOptions(boolean needTurn) {
		this.needTurn = needTurn;
	}

	public boolean needHost() {
		return needHost;
	}
	public boolean needTurn() {
		return needTurn;
	}
}
