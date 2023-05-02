package CLI;

import network.rpc.client.NetworkManager;
import network.rpc.client.Server;
import network.rpc.parameters.Login;

import java.util.ArrayList;

import controller.lobby.Lobby;
import network.rpc.Result;
import network.rpc.server.ClientStatus;

public class CLI {
	private static CLI instance;
	private static NetworkManager networkManager = NetworkManager.getInstance();

	private ClientStatus state;
	private Lobby lobby;

	private String ip;
	private int port;

	private CLI() {
		state = ClientStatus.Disconnected;
	}
	public static CLI getInstance() {
		if(instance == null){
			instance = new CLI();
		}
		return instance;
	}

	public void run() {
		printWelcome();
		while (true) {
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
		String username = Utils.askString("Username: ");
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
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return ClientStatus.InLobbySearch;
		}
	}

	private void updateLobby() {
		try {
			Result result = networkManager.updateLobby().waitResult();
			if (result.isOk()) {
				lobby = ((Result<Lobby>)result).unwrap();
			} else {
				System.out.println("[ERROR] " + result.getException().orElse("List players failed"));
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}
	}

	private ClientStatus inLobby() {
		InLobbyOptions option = Utils.askOption(InLobbyOptions.class);
		Result result;
		try {
			switch (option) {
				case START_GAME -> {  // TODO: maybe check if can be called (update lobby and check owner / n players), need to save username
					result = networkManager.gameStart().waitResult();
					if (result.isOk()) {
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
					updateLobby();
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
		System.out.println("In Game");
		throw new RuntimeException("Not implemented");
	}
}
