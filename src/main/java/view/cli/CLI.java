package view.cli;

import controller.IdentityTheftException;
import controller.MessageTooLongException;
import controller.lobby.LobbyFullException;
import controller.lobby.LobbyNotFoundException;
import network.*;
import network.errors.ClientNotFoundException;
import network.errors.WrongParametersException;
import network.parameters.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import controller.lobby.Lobby;
import model.Cockade;
import model.Point;
import model.Score;
import model.ScoreBoard;
import model.Shelf;
import model.TableTop;
import model.PersonalObjective;

import static network.Server.SERVER_NAME;

/**
 * Singleton class that handles the CLI functions
 * To run the CLI do:
 * `CLI cli = CLI.getInstance();`
 * `cli.run();`
 * @author Marco
 */
public class CLI {
	private static CLI instance;
	public static NetworkManagerInterface networkManager;
	private final Utils IO;
	private static final Logger logger = Logger.getLogger(CLI.class.getName());

	private ClientStatus state;
	private Lobby lobby;
	private boolean needQuit;

	private String ip;
	private int port;

	private String username;

	private boolean isHost;
	private boolean doPrint;
	private boolean gameStarted;
	private boolean yourTurn;
	private boolean isPaused;

	private CLIGame game;

	/**
	 * Class constructor
	 *
	 * @author Marco
	 */
	private CLI() {
		IO = new Utils();
		state = ClientStatus.Disconnected;
		needQuit = false;
		doPrint = true;
		gameStarted = false;
	}

	/**
	 * Return the instance of the `CLI` singleton
	 * @return the `CLI` instance
	 *
	 * @author Marco
	 */
	public static CLI getInstance() {
		if(instance == null){
			instance = new CLI();
		}
		return instance;
	}

	/**
	 * Loop to handle the whole game events
	 * @throws RuntimeException if we fall in a state that does not exist
	 *
	 * @author Marco
	 */
	public void run() {
		printWelcome();
		while (!needQuit) {
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

	/**
	 * Print the initial banner.
	 * It is called only once in the beginning of the game
	 *
	 * @author Marco
	 */
	private void printWelcome() {
		// Hey java where are r-strings?
		System.out.println("  /\\\\\\\\            /\\\\\\\\  /\\\\\\        /\\\\\\               /\\\\\\\\\\\\\\\\\\\\\\    /\\\\\\        /\\\\\\  /\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  /\\\\\\              /\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  /\\\\\\\\\\\\\\\\\\\\\\  /\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println("  \\/\\\\\\\\\\\\        /\\\\\\\\\\\\ \\///\\\\\\    /\\\\\\/              /\\\\\\/////////\\\\\\ \\/\\\\\\       \\/\\\\\\ \\/\\\\\\///////////  \\/\\\\\\             \\/\\\\\\///////////  \\/////\\\\\\///  \\/\\\\\\///////////");
		System.out.println("   \\/\\\\\\//\\\\\\    /\\\\\\//\\\\\\   \\///\\\\\\/\\\\\\/               \\//\\\\\\      \\///  \\/\\\\\\       \\/\\\\\\ \\/\\\\\\             \\/\\\\\\             \\/\\\\\\                 \\/\\\\\\     \\/\\\\\\");
		System.out.println("    \\/\\\\\\\\///\\\\\\/\\\\\\/ \\/\\\\\\     \\///\\\\\\/                  \\////\\\\\\         \\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\/\\\\\\\\\\\\\\\\\\\\\\     \\/\\\\\\             \\/\\\\\\\\\\\\\\\\\\\\\\         \\/\\\\\\     \\/\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println("     \\/\\\\\\  \\///\\\\\\/   \\/\\\\\\       \\/\\\\\\                      \\////\\\\\\      \\/\\\\\\/////////\\\\\\ \\/\\\\\\///////      \\/\\\\\\             \\/\\\\\\///////          \\/\\\\\\     \\/\\\\\\///////");
		System.out.println("      \\/\\\\\\    \\///     \\/\\\\\\       \\/\\\\\\                         \\////\\\\\\   \\/\\\\\\       \\/\\\\\\ \\/\\\\\\             \\/\\\\\\             \\/\\\\\\                 \\/\\\\\\     \\/\\\\\\");
		System.out.println("       \\/\\\\\\             \\/\\\\\\       \\/\\\\\\                  /\\\\\\      \\//\\\\\\  \\/\\\\\\       \\/\\\\\\ \\/\\\\\\             \\/\\\\\\             \\/\\\\\\                 \\/\\\\\\     \\/\\\\\\");
		System.out.println("        \\/\\\\\\             \\/\\\\\\       \\/\\\\\\                 \\///\\\\\\\\\\\\\\\\\\\\\\/   \\/\\\\\\       \\/\\\\\\ \\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\/\\\\\\              /\\\\\\\\\\\\\\\\\\\\\\ \\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println("         \\///              \\///        \\///                    \\///////////     \\///        \\///  \\///////////////  \\///////////////  \\///              \\///////////  \\///////////////");
		System.out.println("");

	}

	/**
	 * Ask for the server information and try to connect to the specified server
	 * with the specified method.
	 * @return `Idle` if the connection succeeds, `Disconnected` otherwise
	 *
	 * @author Marco
	 */
	private ClientStatus connect() {
		askIpAndMethod();

		try {
			networkManager.connect(new Server(this.ip, this.port));
			return ClientStatus.Idle;
		} catch (Exception e) {
			System.out.println("[ERROR] Connection failed");
			logger.warning("Connection failed " + e + " " + e.getMessage());
			return ClientStatus.Disconnected;
		}
	}

	/**
	 * Ask for the server ip and connection method.
	 *
	 * @author Marco
	 */
	private void askIpAndMethod() {
		this.ip = IO.askString("[+] Server IP: ");
		switch (IO.askOption(ConnectionModeOptions.class)) {
			case SOCKET -> {
				this.port = 8000;
				networkManager = network.rpc.client.NetworkManager.getInstance();
			}
			case RMI -> {
				this.port = 8001;
				networkManager = network.rmi.client.NetworkManager.getInstance();
			}
		}
		IO.setNetworkManager(networkManager);
	}

	/**
	 * Handle login.
	 * Ask for username and try to connect with that.
	 * If the connection is successful, check if the user was already in game and
	 * if so, bring him back in the game pre-disconnection.
	 * @return `Idle` if login fails, `InLobbySearch` if new user, `InGame` if reconnected user.
	 *
	 * @author Marco
	 */
	private ClientStatus login() {
		username = IO.askString("[+] Username: ");
		try {
			Result result = networkManager.login(new Login(username)).waitResult();
			if (result.isOk()) {
				if (result.unwrap().equals(Boolean.TRUE)) {
					return ClientStatus.InLobbySearch;
				} else {
					GameInfo gameInfo = (GameInfo)result.unwrap();
					game = new CLIGame(gameInfo, username);
					lobby = gameInfo.lobby();
					yourTurn = gameInfo.currentPlayer().equals(username);
					gameStarted = true;
					if (gameInfo.currentPlayer().equals(username)) {  // shouldn't be possible
						yourTurn = true;
						System.out.println("[*] It's your turn");
					} else {
						yourTurn = false;
						System.out.println("[*] It's " + gameInfo.currentPlayer() + "'s turn");
					}
					return ClientStatus.InGame;
				}
			}
			System.out.println("[ERROR] Login failed (username is already taken)");
			logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Login failed");
		} catch (Exception e) {
			System.out.println("[WARNING] Login failed");
			logger.warning("Login failed " + e + " " + e.getMessage());
		}
		return ClientStatus.Idle;
	}

	/**
	 * Handle the `InLobbySearch` state.
	 * Ask the user what to do:
	 *  - `CREATE_LOBBY`: create a lobby with the given name
	 *  - `JOIN_LOBBY`: join the lobby with the given name
	 *  - `LIST_LOBBIES`: list all the available lobbies
	 *  - `QUIT`: quit the game
	 * @return `InLobbySearch` if any error occurs, `InLobby` if the user entered a lobby,
	 * `InLobbySearch` if the user listed the lobbies and `Disconnected` if the user choose to quit
	 * @throws RuntimeException if we try to handle a non-existing option.
	 *
	 * @author Marco
	 */
	private ClientStatus searchLobby() {
		Optional<SelectLobbyOptions> option = IO.askOptionOrEvent(SelectLobbyOptions.class, true, false, false);
		if (option.isEmpty()) {
			return handleEvent(); // must be `ServerDiscnnected`
		}
		String lobbyName;
		Result result;
		try {
			switch (option.get()) {
				case CREATE_LOBBY -> {
					lobbyName = IO.askString("[+] Lobby name: ");
					result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();
					if (result.isOk()) {
						lobby = ((Result<Lobby>)result).unwrap();
						isHost = true;
						doPrint = true;
						return ClientStatus.InLobby;
					} else {
						System.out.println("[ERROR] Lobby already exists");
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Create lobby failed");
						return ClientStatus.InLobbySearch;
					}
				}
				case JOIN_LOBBY -> {
					lobbyName = IO.askString("[+] Lobby name: ");
					result = networkManager.lobbyJoin(lobbyName).waitResult();
					if (result.isOk()) {
						lobby = ((Result<Lobby>)result).unwrap();
						doPrint = true;
						isHost = false;
						return ClientStatus.InLobby;
					} else {
						Exception e = (Exception)result.getException().get();
						if (e instanceof LobbyFullException) {
							System.out.println("[ERROR] Lobby is full");
						} else if (e instanceof LobbyNotFoundException) {
							System.out.println("[ERROR] Lobby not found");
						} else {
							System.out.println("[ERROR] Join lobby failed" + (e.getMessage() != null ? " " + e.getMessage() : ""));
						}
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Join lobby failed");
						return ClientStatus.InLobbySearch;
					}
				}
				case LIST_LOBBIES -> {
					result = networkManager.lobbyList().waitResult();
					if (result.isOk()) {
						ArrayList<Lobby> lobbies = ((Result<ArrayList<Lobby>>)result).unwrap();
						if (lobbies.isEmpty()) {
							System.out.println("[!] No lobbies available");
						} else {
							for (Lobby lobby : ((Result<ArrayList<Lobby>>)result).unwrap()) {
								System.out.println(String.format(" - %s ( %d players )", lobby.getName(), lobby.getNumberOfPlayers()));
							}
						}
					} else {
						System.out.println("[ERROR] List lobby failed");
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "List lobby failed");
					}
					return ClientStatus.InLobbySearch;
				}
				case QUIT -> {
					networkManager.disconnect();
					networkManager.join();
					needQuit = true;
					System.out.println("[*] Bye bye!");
					return ClientStatus.Disconnected;
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Action failed");
			logger.warning(e + " " + e.getMessage());
			return ClientStatus.InLobbySearch;
		}
	}

	/**
	 * Handle the `InLobby` state.
	 * Ask the user what to do:
	 *  - `SEND_MESSAGE`: send a message to the selected player or everyone
	 *  - `LIST_PLAYERS`: display the players in the lobby (the host is marked with a `+`)
	 *  - `LEAVE_LOBBY`: leave the current lobby
	 *  - `START_GAME`: (host only) start the game with the players in the lobby
	 *  - `LOAD_GAME`: (host only) attempt to load the game from the server
	 * @return `InLobby` if any error occurs or if the player sends a message or list the players,
	 * `InGame` if the host starts or loads a game, `InLobbySearch` if the players leaves the lobby.
	 * @throws RuntimeException if we try to handle a non-existing option.
	 *
	 * @author Marco
	 */
	private ClientStatus inLobby() {
		Optional<InLobbyOptions> option = IO.askOptionOrEvent(InLobbyOptions.class, doPrint, isHost, false);
		if (option.isEmpty()) {
			doPrint = false;
			return handleEvent();
		}
		doPrint = true;
		Result result;
		try {
			switch (option.get()) {
				case SEND_MESSAGE -> {
					sendMessage();
					return ClientStatus.InLobby;
				}
				case LIST_PLAYERS -> {
					for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
						if (i == 0) {
							System.out.printf(" + %s%n", lobby.getPlayers().get(i));
						} else {
							System.out.printf(" - %s%n", lobby.getPlayers().get(i));
						}
					}
					return ClientStatus.InLobby;
				}
				case LEAVE_LOBBY -> {
					result = networkManager.lobbyLeave().waitResult();
					if (result.isOk()) {
						return ClientStatus.InLobbySearch;
					} else {
						System.out.println("[ERROR] Leave lobby failed");
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Leave lobby failed");
						return ClientStatus.InLobby;
					}
				}
				case START_GAME -> {
					if (!checkCanStartGame()) {
						return ClientStatus.InLobby;
					}
					result = networkManager.gameStart().waitResult();
					if (result.isOk()) {
						System.out.println("[*] Starting game");
						return ClientStatus.InGame;
					} else {
						System.out.println("[ERROR] Start game failed");
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Start lobby failed");
						return ClientStatus.InLobby;
					}
				}
				case LOAD_GAME -> {
					if (!checkCanStartGame()) {
						return ClientStatus.InLobby;
					}
					result = networkManager.gameLoad().waitResult();
					if (result.isOk()) {
						System.out.println("[*] Loading game");
						return ClientStatus.InGame;
					} else {
						System.out.println("[ERROR] Load game failed");
						logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Load lobby failed");
						return ClientStatus.InLobby;
					}
				}
				default -> throw new RuntimeException("Invalid option");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Action failed");
			logger.warning(e + " " + e.getMessage());
			return ClientStatus.InLobby;
		}
	}

	/**
	 * Handle the `InGame` state.
	 * Ask the user what to do:
	 *  - `SEND_MESSAGE`: send a message to the selected player or everyone
	 *  - `SHOW_YOUR_SHELF`: display the player's shelf
	 *  - `SHOW_ALL_SHELVES`: display all the players' shelves
	 *  - `SHOW_TABLETOP`: display the current tabletop
	 *  - `SHOW_PERSONAL_OBJECTIVE`: display the player's personal objective
	 *  - `SHOW_COMMON_OBJECTIVES`: display the common objectives
	 *  - `STOP_GAME`: (host only) tell the server to save and stop the game
	 *  - `PICK_CARDS: ask the player for the cards he wants to pick. This option is available only during the player's turn
	 * If the player is the host and jus started the game, he has to wait for the `Start` server event.
	 * @return `InGame` if any error occurs or if the players sends a message or displays anything, otherwise
	 * it returns whatever the function corresponding to the selected options returns.
	 * @throws RuntimeException if we try to handle a non-existing option.
	 *
	 * @author Marco
	 */
	private ClientStatus inGame() {
		if (!gameStarted) {
			return waitGlobalUpdate();
		}

		Optional<InGameOptions> option = IO.askOptionOrEvent(InGameOptions.class, doPrint, isHost, yourTurn && !isPaused);
		if (option.isEmpty()) {
			doPrint = false;
			return handleEvent();
		}
		doPrint = true;

		switch (option.get()) {
			case SEND_MESSAGE -> {
				sendMessage();
				return ClientStatus.InGame;
			}
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
			case STOP_GAME -> {
				return handleStopGame();
			}
			case PICK_CARDS -> {
				return handlePickCard();
			}
			default -> throw new RuntimeException("Invalid option");
		}
	}

	/**
	 * Checks if the player has the ability to start the game, so if it is the host of the lobby
	 * and if we have at least two players.
	 * @return if the player can start the game
	 *
	 * @author Marco
	 */
	private boolean checkCanStartGame() {
		if (!lobby.isHost(username)) {  // This is not needed
			System.out.println("[ERROR] You are not the lobby's host");
			return false;
		} else if (lobby.getNumberOfPlayers() < 2) {
			System.out.println("[ERROR] Not enough players to start the game");
			return false;
		}
		return true;
	}

	/**
	 * Convert an input string to a `Point` object with the corresponding coordinates.
	 * @param line a string with the wanted coordinates in the form (`a1` or `1a`)
	 * @return the `Point` object corresponding to the input coordinates.
	 *
	 * @author Marco
	 */
	private Point stringToPoint(String line) {
		int y, x;
		if ('0' <= line.charAt(0) && line.charAt(0) <= '9') {
			y = line.charAt(0) - '1';
			x = line.charAt(1) - 'a';
		} else {
			y = line.charAt(1) - '1';
			x = line.charAt(0) - 'a';
		}
		y = TableTop.SIZE - y - 1;
		if (game.getNumberOfPlayers() == 2) {
			y--;
			x++;
		}
		return new Point(y, x);
	}

	/**
	 * Handle the `PICK_CARDS` interaction.
	 * Ask the player which cards to pick and in which column to place those.
	 * @return `InGame` if any error occurs or if the player aborts the selection,
	 * otherwise what the `waitGlobalUpdate` call returns
	 *
	 * @author Marco
	 */
	private ClientStatus handlePickCard() {
		ArrayList<Point> selectedCards = new ArrayList<>();
		int column;

		game.printTableTop();
		System.out.println("[+] Enter the coordinates of the cards you want to pick (-1 to abort)");
		for (int i = 0; i < 3; i++) {
			boolean ok = false;
			while (!ok) {
				String line = IO.askString();
				line = line.toLowerCase().replaceAll("[^a-z0-9-]", "");
				if (line.isEmpty()) {
					break;
				}
				if (line.length() != 2) {
					System.out.println("[!] Invalid coordinates");
				} else if (line.equals("-1")) {
					System.out.println("[*] Aborted");
					return ClientStatus.InGame;
				} else {
					Point p = stringToPoint(line);
					if (p.x() < 0 || p.x() >= TableTop.SIZE || p.y() < 0 || p.y() >= TableTop.SIZE) {
						System.out.println("[!] Invalid coordinates");
					} else if (game.tableTop[p.y()][p.x()].isEmpty()) {
						System.out.println("[!] Cannot pick card from empty slot");
					} else {
						selectedCards.add(p);
						ok = true;
					}
				}
			}
			if (!ok) {
				if (i == 0) {
					System.out.println("[*] No cards selected, aborting");
					return ClientStatus.InGame;
				}
				break;
			}
		}

		game.printYourShelf();
		System.out.println("[+] Enter the column where you want to place the cards (-1 to abort)");
		while (true) {
			column = IO.askInt() - 1;
			if (column == -2) {
				System.out.println("[*] Aborted");
				return ClientStatus.InGame;
			} else if (column < 0 || column >= Shelf.COLUMNS) {
				System.out.println("[!] Invalid column");
			} else {
				break;
			}
		}

		try {
			Result result = networkManager.cardSelect(new CardSelect(column, selectedCards)).waitResult();
			if (result.isErr()) {
				System.out.println("[ERROR] " + (result.getException().isPresent() ? ((Exception)result.getException().get()).getMessage() : "Cannot select cards"));
				logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Pick cards failed");
			} else {
				return waitGlobalUpdate();
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Pick cards failed");
			logger.warning("Pick cards failed " + e + " " + e.getMessage());
		}

		return ClientStatus.InGame;
	}

	/**
	 * Handle the `STOP_GAME` interaction.
	 * Ask the host for confirmation and send to the server the request to stop the game.
	 * @return `InGame` if any error occurs or if the player aborts the operation, otherwise what the `waitGlobalUpdate` call returns
	 *
	 * @author Marco
	 */
	private ClientStatus handleStopGame() {
		String yn = IO.askString("Do you really want to stop the game (y/n)? ");
		if (!yn.isEmpty() && yn.toLowerCase().charAt(0) == 'y') {
			try {
				Result result = networkManager.exitGame().waitResult();
				if (result.isErr()) {
					System.out.println("[ERROR] Stop game failed");
					logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Stop game failed");
				} else {
					return waitGlobalUpdate();
				}
			} catch (Exception e) {
				System.out.println("[ERROR] Stop game failed");
				logger.warning(e + " " + e.getMessage());
			}
		}
		return ClientStatus.InGame;
	}

	/**
	 * Handle the `SEND_MESSAGE` interaction.
	 * Ask the player for the receiver and the message to send.
	 *
	 * @author Marco
	 */
	private void sendMessage() {
		System.out.println();
		int cnt = 0;
		for (String player : lobby.getPlayers()) {
			if (!player.equals(username)) {
				System.out.format("[%d] %s%n", ++cnt, player);
			}
		}
		int selected = IO.askInt("Receiver (0 for everyone): ");
		if (selected < 0 || selected >= lobby.getNumberOfPlayers()) {
			System.out.println("[*] Aborted");
			return;
		}
		cnt = 0;
		String receiver = null;
		if (selected > 0) {
			for (String player : lobby.getPlayers()) {
				if (!player.equals(username)) {
					if (++cnt == selected) {
						receiver = player;
						break;
					}
				}
			}
		}

		String message = IO.askString("[+] Message: ");
		try {
			Result result = networkManager.chat(new Message(username, message, receiver)).waitResult();
			if (result.isErr()) {
				Exception e = (Exception)result.getException().get();
				if (e instanceof WrongParametersException) {
					System.out.println("[ERROR] " + e.getMessage());
				} else if (e instanceof MessageTooLongException) {
					System.out.println("[ERROR] Message is too long");
				} else if (e instanceof IdentityTheftException) {
					System.out.println("[ERROR] Identity theft is not a joke, Bob!");
				} else if (e instanceof ClientNotFoundException) {
					System.out.println("[ERROR] The player has disconnected");
				} else {
					System.out.println("[ERROR] Send message failed" + (e.getMessage() != null ? " " + e.getMessage() : ""));
				}
				logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Stop game failed");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Send message failed");
			logger.warning("Send message failed " + e + " " + e.getMessage());
		}
	}

	/**
	 * Print the scoreboard as received from the server.
	 * @param scoreboard The scoreboard to print, a `Scoreboard` object
	 *
	 * @author Marco
	 */
	private void printEndGame(ScoreBoard scoreboard) {
		String your_title = "HEY! Where is my title?";
		System.out.println("[*] Game over!");
		System.out.println();
		System.out.println("Leaderboard:");
		int position = 1;
		for (Score score : scoreboard) {
			System.out.format(" [%d] %s: %d points %n", position++, score.username(), score.score());
			if (score.username().equals(username)) {
				your_title = score.title();
			}
		}
		System.out.println();
		System.out.format("%nYour final grade: %s%n%n", your_title);
		System.out.println();

		System.out.println("Cockades:");
		ArrayList<Cockade> yourCockades = scoreboard.getCockades(username);
		if (yourCockades.isEmpty()) {
			System.out.println("You: not even a single cockade, what a shame");
		} else {
			System.out.println("You:");
			for (Cockade cockade : yourCockades) {
				System.out.format("  - %s ( %d points )%n", cockade.name().indexOf(PersonalObjective.MARKER) == 0 ? "Personal objective" : cockade.name(), cockade.points());
			}
		}

		for (String player : lobby.getPlayers()) {
			if (player.equals(username)) {
				continue;
			}
			ArrayList<Cockade> cockades = scoreboard.getCockades(player);
			if (cockades.isEmpty()) {
				System.out.format("%s: not even a single cockade, what a looser%n", player);
			} else {
				System.out.format("%s:%n", player);
				for (Cockade cockade : cockades) {
					System.out.format("  - %s ( %d points )%n", cockade.name().indexOf(PersonalObjective.MARKER) == 0 ? "Personal objective" : cockade.name(), cockade.points());
				}
			}
		}
	}

	/**
	 * Asynchronously wait for a global update from the server.
	 * @return The status of the client after the update or the current status if an error occurs
	 *
	 * @author Marco
	 */
	private ClientStatus waitGlobalUpdate() {
		try {
			synchronized (networkManager) {
				while (!networkManager.hasEvent()) {
					networkManager.wait();
				}
			}
		} catch (InterruptedException e) {
			return state;
		}
		return handleEvent();
	}

	/**
	 * Handler for the server events.
	 * The handled events are:
	 *  - `Join`: a new player joins the lobby
	 *  - `Leave`: a player leaves the lobby
	 *  - `Start`: the game starts
	 *  - `Update`: The game state is updated after someone moved
	 *  - `End`: The game ends
	 *  - `NewMessage`: A message is received
	 *  - `ExitGame`: The host stopped the game
	 *  - `ServerDisconnect`: The server connection is lost
	 * @return The previous state by default, `InGame` for the `Start` event,
	 * `InLobbySearch` for the `ExitGame` and `End` event and `Disconnected` for the `ServerDisconnect` event
	 * @throws RuntimeException if:
	 *	- We call this method with an empty event queue
	 *	- We try to handle a non-existing event
	 *	- An internal error (not caused by the server) occurs during the event handling (should never happen)
	 *
	 * @author Marco
	 */
	private ClientStatus handleEvent() {
		Optional<ServerEvent> event = networkManager.getEvent();
		if (event.isEmpty()) {
			throw new RuntimeException("Empty event queue");
		}
		switch (event.get().getType()) {
			case Join -> {
				String joinedPlayer = (String)event.get().getData();
				if (!lobby.getPlayers().contains(joinedPlayer)) {
					try {
						lobby.addPlayer(joinedPlayer);
					} catch (Exception e) {
						throw new RuntimeException("Added already existing player to lobby");
					}
					System.out.println("[*] " + joinedPlayer + " joined the lobby");
				}
			}
			case Leave -> {
				String leftPlayer = (String)event.get().getData();
				if (lobby.getPlayers().contains(leftPlayer)) {
					boolean wasHost;
					try {
						wasHost = isHost;
						lobby.removePlayer(leftPlayer);
						isHost = lobby.isHost(username);
					} catch (Exception e) {
						throw new RuntimeException("Removed non existing player from lobby");
					}
					System.out.format("[*] %s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
					if (!wasHost && isHost) {
						System.out.println("[*] You are now the host");
						doPrint = true;
					}
				}
			}
			case Start -> {
				gameStarted = true;
				doPrint = true;
				GameInfo gameInfo = (GameInfo)event.get().getData();
				game = new CLIGame(gameInfo, username);
				yourTurn = gameInfo.currentPlayer().equals(username);
				System.out.println("[*] Game has started");
				if (yourTurn) {
					System.out.println("[*] It's your turn");
				} else {
					System.out.println("[*] It's " + game.players.get(0) + "'s turn");
				}
				return ClientStatus.InGame;
			}
			case Update -> {
				Update update = (Update)event.get().getData();
				for (Cockade commonObjective : update.completedObjectives()) {
					if (update.idPlayer().equals(username)) {
						System.out.format("[*] You completed %s getting %d points%n", commonObjective.name(), commonObjective.points());
					} else {
						System.out.format("[*] %s completed %s getting %d points%n", update.idPlayer(), commonObjective.name(), commonObjective.points());
					}
				}
				game.update(update);
				doPrint = true;
				if (update.nextPlayer().equals(username)) {
					yourTurn = true;
					System.out.println("[*] It's your turn");
				} else {
					yourTurn = false;
					System.out.println("[*] It's " + update.nextPlayer() + "'s turn");
				}
			}
			case End -> {
				ScoreBoard scoreboard = (ScoreBoard)event.get().getData();
				printEndGame(scoreboard);
				IO.askString("[+] Press enter to continue");
				doPrint = true;
				return ClientStatus.InLobbySearch;
			}
			case NewMessage -> {
				Message message = (Message)event.get().getData();
				if (!message.idSender().equals(username)) {
					if (message.idReceiver().isEmpty()) {
						if (message.idSender().equals(SERVER_NAME)) {
							System.out.format("[%s]: %s%n", message.idSender(), message.message());
						} else {
							System.out.format("[%s to everyone]: %s%n", message.idSender(), message.message());
						}
					} else {
						System.out.format("[%s to you]: %s%n", message.idSender(), message.message());
					}
				}
			}
			case ExitGame -> {
				System.out.println("[*] Game has been stopped");
				return ClientStatus.InLobbySearch;
			}
			case ServerDisconnect -> {
				System.out.println("[WARNING] Server disconnected");
				networkManager.disconnect();
				doPrint = true;
				return ClientStatus.Disconnected;
			}
			default -> throw new RuntimeException("Unhandled event");
		}
		return state;
	}
}
