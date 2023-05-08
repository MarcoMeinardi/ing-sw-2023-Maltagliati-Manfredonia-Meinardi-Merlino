package cli;

import network.rpc.client.NetworkManager;
import network.Server;
import network.parameters.CardSelect;
import network.parameters.Login;
import network.parameters.Message;
import network.parameters.StartingInfo;
import network.parameters.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import controller.lobby.Lobby;
import model.Point;
import model.Shelf;
import model.TableTop;
import network.Result;
import network.ServerEvent;
import network.ClientStatus;

public class CLI {
	private static CLI instance;
	private static NetworkManager networkManager = NetworkManager.getInstance();

	private ClientStatus state;
	private Lobby lobby;
	boolean hasConnected;

	private String ip;
	private int port;

	String username;

	boolean doPrint;
	boolean gameStarted;
	boolean yourTurn;

	CLIGame game;

	private CLI() {
		state = ClientStatus.Disconnected;
		hasConnected = false;
		doPrint = true;
		gameStarted = false;
	}
	public static CLI getInstance() {
		if(instance == null){
			instance = new CLI();
		}
		return instance;
	}

	public void run() {
		printWelcome();
		while (state != ClientStatus.Disconnected || !hasConnected) {
			switch (state) {
				case Disconnected -> state = connect();
				case Idle -> state = login();
				case InLobbySearch -> state = searchLobby();
				case InLobby -> state = inLobby();
				case InGame -> state = inGame();
				default -> throw new RuntimeException ("Invalid state");
			}
		}
	}

	private void printWelcome() {
		System.out.println("Welcome to this beautiful game, that has been accidentally written in Rust");
	}

	private ClientStatus connect() {
		// askIpPort();
		this.ip = "localhost";
		this.port = 8000;
		try {
			networkManager.connect(new Server(this.ip, this.port));
			hasConnected = true;
			return ClientStatus.Idle;
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return ClientStatus.Disconnected;
		}
	}

	private void askIpPort() {
		this.ip = Utils.askString("Server IP: ");
		this.port = Utils.askInt("Server port: ");
	}

	// TODO we need to receive the previous status in case of a reconnection
	private ClientStatus login() {
		username = Utils.askString("Username: ");
		try {
			Result result = networkManager.login(new Login(username)).waitResult();
			if (result.isOk()) {
				return ClientStatus.InLobbySearch;
			}
			System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}
		return ClientStatus.Idle;
	}

	private ClientStatus searchLobby() {
		SelectLobbyOptions option = Utils.askOption(SelectLobbyOptions.class);
		String lobbyName;
		Result result;
		try {
			switch (option) {
				case CREATE_LOBBY -> {
					lobbyName = Utils.askString("Lobby name: ");
					result = networkManager.lobbyCreate(lobbyName).waitResult();
					if (result.isOk()) {
						lobby = ((Result<Lobby>)result).unwrap();
						return ClientStatus.InLobby;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
						return ClientStatus.InLobbySearch;
					}
				}
				case JOIN_LOBBY -> {
					lobbyName = Utils.askString("Lobby name: ");
					result = networkManager.lobbyJoin(lobbyName).waitResult();
					if (result.isOk()) {
						lobby = ((Result<Lobby>)result).unwrap();
						return ClientStatus.InLobby;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Join failed"));
						return ClientStatus.InLobbySearch;
					}
				}
				case LIST_LOBBIES -> {
					result = networkManager.lobbyList().waitResult();
					if (result.isOk()) {
						ArrayList<Lobby> lobbies = ((Result<ArrayList<Lobby>>) result).unwrap();
						if (lobbies.isEmpty()) {
							System.out.println("No lobbies available");
						} else {
							for (Lobby lobby : ((Result<ArrayList<Lobby>>) result).unwrap()) {
								System.out.println(String.format(" - %s ( %d players )", lobby.getName(), lobby.getNumberOfPlayers()));
							}
						}
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("List lobbies failed"));
					}
					return ClientStatus.InLobbySearch;
				}
				case QUIT -> {
					networkManager.disconnect();
					networkManager.join();
					System.out.println("Bye bye!");
					return ClientStatus.Disconnected;
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return ClientStatus.InLobbySearch;
		}
	}

	boolean checkCanStartGame() {
		if (!lobby.isHost(username)) {
			System.out.println("[ERROR] You are not the lobby's host");
			return false;
		} else if (lobby.getNumberOfPlayers() < 2) {
			System.out.println("[ERROR] Not enough players to start the game");
			return false;
		}
		return true;
	}

	private ClientStatus inLobby() {
		Optional<InLobbyOptions> option = Utils.askOptionOrEvent(InLobbyOptions.class, doPrint);
		if (option.isEmpty()) {
			doPrint = false;
			return handleEvent();
		}
		doPrint = true;
		Result result;
		try {
			switch (option.get()) {
				case START_GAME -> {
					if (!checkCanStartGame()) {
						return ClientStatus.InLobby;
					}
					result = networkManager.gameStart().waitResult();
					if (result.isOk()) {
						System.out.println("Starting game");
						return ClientStatus.InGame;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Start game failed"));
						return ClientStatus.InLobby;
					}
				}
				case LEAVE_LOBBY -> {
					result = networkManager.lobbyLeave().waitResult();
					if (result.isOk()) {
						return ClientStatus.InLobbySearch;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Leave lobby failed"));
						return ClientStatus.InLobby;
					}
				}
				case LIST_PLAYERS -> {
					for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
						if (i == 0) {
							System.out.println(String.format(" + %s", lobby.getPlayers().get(i)));
						} else {
							System.out.println(String.format(" - %s", lobby.getPlayers().get(i)));
						}
					}
					return ClientStatus.InLobby;
				}
				case SEND_MESSAGE -> {
					String message = Utils.askString("Message: ");
					result = networkManager.chat(message).waitResult();
					if (result.isErr()) {
						System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
					}
					return ClientStatus.InLobby;
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return ClientStatus.InLobby;
		}
	}

	private ClientStatus inGame() {
		if (!gameStarted) {
			try {  // Wait to receive game start event before promting to abort
				for (int i = 0; i < 20; i++) {
					Thread.sleep(50);
					if (networkManager.hasEvent()) {
						return handleEvent();
					}
				}
			} catch (InterruptedException e) {}

			System.out.println("Waiting for other players...");
			// TODO ask to abort
			throw new RuntimeException("Abort not implemented");
			// Optional<InLobbyOptions> option = Utils.askOptionOrEvent(EarlyAbortOptions.class, true);
			// if (option.isEmpty()) {
			// 	return handleEvent();
			// }
		}

		if (yourTurn) {
			return inGameTurn();
		} else {
			return inGameNoTurn();
		}
	}

	private ClientStatus inGameTurn() {
		Optional<InGameTurnOptions> option = Utils.askOptionOrEvent(InGameTurnOptions.class, doPrint);
		if (option.isEmpty()) {
			doPrint = false;
			return handleEvent();
		}
		switch (option.get()) {
			case SHOW_YOUR_SHELF -> {
				game.printYourShelf();
				return ClientStatus.InGame;
			}
			case SHOW_ALL_SHELVES -> {
				game.printAllShelves();
				return ClientStatus.InGame;
			}
			case SHOW_TABLETOP -> {
				game.printTableTop();
				return ClientStatus.InGame;
			}
			case SHOW_PERSONAL_OBJECTIVE -> {
				game.printPersonalObjective();
				return ClientStatus.InGame;
			}
			case SHOW_COMMON_OBJECTIVES -> {
				game.printCommonObjectives();
				return ClientStatus.InGame;
			}
			case SEND_MESSAGE -> {
				String message = Utils.askString("Message: ");
				try {
					Result result = networkManager.chat(message).waitResult();
					if (result.isErr()) {
						System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
					}
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
				return ClientStatus.InGame;
			}
			case PICK_CARDS -> {
				handlePickCard();
				return ClientStatus.InGame;
			}
			case LEAVE_GAME -> {
				throw new RuntimeException("Not implemented");
				// return ClientStatus.InLobbySearch;
			}
			default -> throw new RuntimeException("Invalid option");
		}
	}

	private ClientStatus inGameNoTurn() {
		Optional<InGameNoTurnOptions> option = Utils.askOptionOrEvent(InGameNoTurnOptions.class, doPrint);
		if (option.isEmpty()) {
			doPrint = false;
			return handleEvent();
		}
		switch (option.get()) {
			case SHOW_YOUR_SHELF -> {
				game.printYourShelf();
				return ClientStatus.InGame;
			}
			case SHOW_ALL_SHELVES -> {
				game.printAllShelves();
				return ClientStatus.InGame;
			}
			case SHOW_TABLETOP -> {
				game.printTableTop();
				return ClientStatus.InGame;
			}
			case SHOW_PERSONAL_OBJECTIVE -> {
				game.printPersonalObjective();
				return ClientStatus.InGame;
			}
			case SHOW_COMMON_OBJECTIVES -> {
				game.printCommonObjectives();
				return ClientStatus.InGame;
			}
			case SEND_MESSAGE -> {
				String message = Utils.askString("Message: ");
				try {
					Result result = networkManager.chat(message).waitResult();
					if (result.isErr()) {
						System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
					}
				} catch (Exception e) {
					System.out.println("[ERROR] " + e.getMessage());
				}
				return ClientStatus.InGame;
			}
			case LEAVE_GAME -> {
				throw new RuntimeException("Not implemented");
				// return ClientStatus.InLobbySearch;
			}
			default -> throw new RuntimeException("Invalid option");
		}
	}

	private Point stringToPoint(String line) {
		char[] chars = line.toCharArray();
		if (chars[0] >= '0' && chars[0] <= '9') {
			Collections.reverse(Arrays.asList(chars));
		}
		int x = chars[0] - 'a';
		int y = chars[1] - '1';
		return new Point(y, x);
	}

	private void handlePickCard() {
		ArrayList<Point> selectedCards = new ArrayList<>();
		int column;

		game.printTableTop();
		System.out.println("Enter the coordinates of the cards you want to pick");
		for (int i = 0; i < 3; i++) {
			boolean ok = false;
			while (!ok) {
				String line = Utils.askString();
				line = line.toLowerCase().replaceAll("\\W", "");
				if (line.isEmpty()) {
					break;
				}
				if (line.length() != 2) {
					System.out.println("Invalid coordinates");
				} else {
					Point p = stringToPoint(line);
					if (p.x() < 0 || p.x() >= TableTop.SIZE || p.y() < 0 || p.y() >= TableTop.SIZE) {
						System.out.println("Invalid coordinates");
					} else if (game.tableTop[p.y()][p.x()].isEmpty()) {
						System.out.println("Cannot pick card from empty slot");
					} else {
						selectedCards.add(p);
						ok = true;
					}
				}
			}
			if (!ok) {
				if (i == 0) {
					System.out.println("No cards selected, aborting");
					return;
				}
				break;
			}
		}

		game.printYourShelf();
		System.out.println("Enter the column where you want to place the cards (-1 to abort)");
		while (true) {
			column = Utils.askInt() - 1;
			if (column == -2) {
				System.out.println("Aborted");
				return;
			} else if (column < 0 || column >= Shelf.COLUMNS) {
				System.out.println("Invalid column");
			} else {
				break;
			}
		}

		try {
			Result result = networkManager.cardSelect(new CardSelect(column, selectedCards)).waitResult();
			if (result.isErr()) {
				System.out.println("[ERROR] " + result.getException().orElse("Cannot select cards"));
			} else {
				waitGlobalUpdate();
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}

	}

	public void waitGlobalUpdate() {
		try {
			while (!networkManager.hasEvent()) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {}
		if (handleEvent() != ClientStatus.InGame) {
			throw new RuntimeException("Invalid status");
		}
	}

	private ClientStatus handleEvent() {
		Optional<ServerEvent> event = networkManager.getEvent();
		if (event.isEmpty()) {
			throw new RuntimeException("Empty event queue");
		}
		switch (event.get().getType()) {
			case Join -> {
				String joinedPlayer = (String)event.get().getData();
				try {
					lobby.addPlayer(joinedPlayer);
				} catch (Exception e) {}  // Cannot happen
				if (!joinedPlayer.equals(username)) {
					System.out.println(joinedPlayer + " joined the lobby");
				}
			}
			case Leave -> {
				String leftPlayer = (String)event.get().getData();
				try {
					lobby.removePlayer(leftPlayer);
				} catch (Exception e) {}  // Cannot happen
				System.out.format("%s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
			}
			case Start -> {
				gameStarted = true;
				doPrint = true;
				game = new CLIGame((StartingInfo)event.get().getData(), username);
				yourTurn = game.players.get(0).equals(username);
				System.out.println("Game has started");
				if (yourTurn) {
					System.out.println("It's your turn");
				} else {
					System.out.println("It's " + game.players.get(0) + "'s turn");
				}
				return ClientStatus.InGame;
			}
			case Update -> {
				Update update = (Update)event.get().getData();
				game.update(update);
				doPrint = true;
				if (update.nextPlayer().equals(username)) {
					yourTurn = true;
					System.out.println("It's your turn");
				} else {
					yourTurn = false;
					System.out.println("It's " + update.nextPlayer() + "'s turn");
				}
			}
			case NewMessage -> {
				Message message = (Message)event.get().getData();
				if (!message.idPlayer().equals(username)) {
					System.out.format("%s: %s%n", message.idPlayer(), message.message());
				}
			}
			default -> throw new RuntimeException("Unhandled exception");
		}
		return state;
	}
}

