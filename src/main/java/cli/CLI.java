package cli;

import network.rpc.client.NetworkManager;
import network.Server;
import network.parameters.Login;
import network.parameters.StartingInfo;

import java.util.ArrayList;
import java.util.Optional;

import controller.lobby.Lobby;
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
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return ClientStatus.InLobby;
		}
	}

	private ClientStatus inGame() {
		if (!gameStarted) {
			try {
				Thread.sleep(1000);  // Wait to receive game start event before promting to abort
			} catch (InterruptedException e) {}

			if (networkManager.hasEvent()) {
				return handleEvent();
			}
			System.out.println("Waiting for other players...");
			// TODO ask to abort
			Optional<InLobbyOptions> option = Utils.askOptionOrEvent(InLobbyOptions.class, doPrint);
			if (option.isEmpty()) {
				return handleEvent();
			} else {
				throw new RuntimeException("Abort not implemented");
			}
		}
		throw new RuntimeException("Not implemented");
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
				return ClientStatus.InLobby;
			}
			case Leave -> {
				String leftPlayer = (String)event.get().getData();
				try {
					lobby.removePlayer(leftPlayer);
				} catch (Exception e) {}  // Cannot happen
				System.out.println(leftPlayer + " left the lobby");
				return ClientStatus.InLobby;
			}
			case Start -> {
				gameStarted = true;
				doPrint = true;
				game = new CLIGame((StartingInfo)event.get().getData());
				System.out.println("Game has started");
				return ClientStatus.InGame;
			}
			default -> throw new RuntimeException("Unhandled exception");
		}
	}
}
