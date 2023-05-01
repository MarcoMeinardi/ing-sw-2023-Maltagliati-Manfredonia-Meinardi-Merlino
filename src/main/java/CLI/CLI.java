package CLI;

import network.rpc.client.NetworkManager;
import network.rpc.client.Server;
import network.rpc.parameters.Login;

import java.util.ArrayList;

import controller.lobby.Lobby;
import network.rpc.Result;

public class CLI {
	private static CLI instance;
	private static NetworkManager networkManager = NetworkManager.getInstance();

	private CLIState state;
	private Lobby lobby;

	private String ip;
	private int port;

	private CLI() {
		state = CLIState.CONNECT;
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
				case CONNECT -> state =	connect();
				case LOGIN -> state = login();
				case SELECT_LOBBY -> state = selectLobby();
				case IN_LOBBY -> state = inLobby();
				case IN_GAME -> state = inGame();
				default -> {
					throw new RuntimeException ("Invalid state");
				}
			}
		}
	}

	private void printWelcome() {
		System.out.println("Welcome to this beautiful game, that has been accidentally written in Rust");
	}

	private CLIState connect() {
		askIpPort();
		try {
			networkManager.connect(new Server(this.ip, this.port));
			return CLIState.LOGIN;
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return CLIState.CONNECT;
		}
	}

	private void askIpPort() {
		this.ip = Utils.askString("Server IP: ");
		this.port = Utils.askInt("Server port: ");
	}

	// TODO we need to receive the previous status in case of a reconnection
	private CLIState login() {
		String username = Utils.askString("Username: ");
		try {
			Result result = networkManager.login(new Login(username)).waitResult();
			if (result.isOk()) {
				return CLIState.SELECT_LOBBY;
			}
			System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}
		return CLIState.LOGIN;
	}

	private CLIState selectLobby() {
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
						return CLIState.IN_LOBBY;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
						return CLIState.SELECT_LOBBY;
					}
				}
				case JOIN_LOBBY -> {
					lobbyName = Utils.askString("Lobby name: ");
					result = networkManager.lobbyJoin(lobbyName).waitResult();
					if (result.isOk()) {
						lobby = ((Result<Lobby>)result).unwrap();
						return CLIState.IN_LOBBY;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Join failed"));
						return CLIState.SELECT_LOBBY;
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
					return CLIState.SELECT_LOBBY;
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return CLIState.SELECT_LOBBY;
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

	private CLIState inLobby() {
		InLobbyOptions option = Utils.askOption(InLobbyOptions.class);
		Result result;
		try {
			switch (option) {
				case START_GAME -> {  // TODO: maybe check if can be called (update lobby and check owner / n players), need to save username
					result = networkManager.gameStart().waitResult();
					if (result.isOk()) {
						return CLIState.IN_GAME;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Start game failed"));
						return CLIState.IN_LOBBY;
					}
				}
				case LEAVE_LOBBY -> {
					result = networkManager.lobbyLeave().waitResult();
					if (result.isOk()) {
						return CLIState.SELECT_LOBBY;
					} else {
						System.out.println("[ERROR] " + result.getException().orElse("Leave lobby failed"));
						return CLIState.IN_LOBBY;
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
					return CLIState.IN_LOBBY;
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
			return CLIState.IN_LOBBY;
		}
	}

	private CLIState inGame() {
		System.out.println("In Game");
		throw new RuntimeException("Not implemented");
	}
}
