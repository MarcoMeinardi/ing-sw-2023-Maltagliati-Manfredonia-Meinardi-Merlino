package view.cli;

import java.util.Optional;

import network.NetworkManagerInterface;


/**
 * Utilities class for the CLI IO
 * To use do: `Utils utils = new Utils();`
 * and once you have the network manager: `utils.setNetworkManager(networkManager);`
 * this is needed, because we want to return the control to the CLI if an event arrived.
 * Not that you can't use non blocking options before the network manager has been set.
 *
 * @author Marco
 */
public class Utils {
	private NetworkManagerInterface networkManager = null;
	private final NonBlockingIO IO;

	/**
	 * Class constructor
	 *
	 * @author Marco
	 */
	public Utils() {
		this.IO = NonBlockingIO.getInstance();
	}

	/**
	 * Set the network manager
	 * @param networkManager the network manager instance
	 *
	 * @author Marco
	 */
	public void setNetworkManager(NetworkManagerInterface networkManager) {
		this.networkManager = networkManager;
		IO.setNetworkManager(networkManager);
	}

	/**
	 * Read a string (blocking)
	 * @return the read string
	 *
	 * @author Marco
	 */
	public String askString() {
		IO.ask();
		try {
			while (!IO.isAvailable()) {
				Thread.sleep(50);  // We cannot use the trick of synchronized and waits, because the network manager might not have been initialized yet
			}
		} catch (InterruptedException e) {
			return null;
		}
		return IO.getResult();
	}
	
	/**
	 * Print the given message and read a string (blocking)
	 * @param message the message to print
	 * @return the read string
	 *
	 * @author Marco
	 */
	public String askString(String message) {
		System.out.print(message);
		return askString();
	}

	/**
	 * Read a string or return an event
	 * @return If an event arrived, an empty optional, otherwise, an optional containing the read string
	 *
	 * @author Marco
	 */
	public Optional<String> askStringOrEvent() {
		if (networkManager.hasEvent()) {
			return Optional.empty();
		}
		IO.ask();
		try {
			synchronized (networkManager) {
				while (!IO.isAvailable() && !networkManager.hasEvent()) {
					networkManager.wait();
				}
				if (!IO.isAvailable() && networkManager.hasEvent()) {
					return Optional.empty();
				}
			}
		} catch (InterruptedException e) {
			return Optional.empty();
		}
		return Optional.of(IO.getResult());
	}
	/**
	 * Print the given message and read a string or wait for an event
	 * @return If an event arrived, an empty optional, otherwise, an optional containing the read string
	 *
	 * @author Marco
	 */
	public Optional<String> askStringOrEvent(String message) {
		System.out.print(message);
		return askStringOrEvent();
	}

	/**
	 * Read an integer (blocking)
	 * @return the read integer
	 *
	 * @author Marco
	 */
	public int askInt() {
		int result = -1;
		String line = askString();
		try {
			result = Integer.parseInt(line);
		} catch (Exception e) {}
		return result;
	}

	/**
	 * Print the given message and read an integer (blocking)
	 * @return the read integer
	 *
	 * @author Marco
	 */
	public int askInt(String message) {
		System.out.print(message);
		return askInt();
	}

	/**
	 * Read an integer or wait for an event
	 * @return If an event arrived, an empty optional, otherwise, an optional containing the read integer
	 *
	 * @author Marco
	 */
	public Optional<Integer> askIntOrEvent() {
		Optional<String> line = askStringOrEvent();
		if (line.isPresent()) {
			try {
				return Optional.of(Integer.parseInt(line.get()));
			} catch (Exception e) {
				return Optional.of(-1);
			}
		}
		return Optional.empty();
	}

	/**
	 * Print the given message and read an integer or wait for an event
	 * @return If an event arrived, an empty optional, otherwise, an optional containing the read string
	 *
	 * @author Marco
	 */
	public Optional<Integer> askIntOrEvent(String message) {
		System.out.print(message);
		return askIntOrEvent();
	}

	/**
	 * Convert an enum to a string
	 * The enum name has to be underscore separated words. The first letter will be capitalized and the followings will be lowered.
	 * Underscores will be converted in spaces.
	 * Example: `MY_ENUM` will be converted to `My enum`
	 * @param e the enum to convert
	 *
	 * @author Marco
	 */
	private <E extends Enum<E> & OptionsInterface> String enumToOption(E e) {  // E is an enum that implements OptionsInterface
		String repr = e.toString();
		return repr.substring(0, 1).toUpperCase() + repr.substring(1).toLowerCase().replace('_', ' ');
	}

	/**
	 * Read an option (blocking)
	 * The enum names will be converted by the `enumToOption` method and used as a prompt.
	 * @param enumClass the class of the enum that implements `OptionsInterface` from which to chose the option
	 * @return the read option
	 *
	 * @author Marco
	 */
	public <E extends Enum<E> & OptionsInterface> E askOption(Class<E> enumClass) {
		return askOption(enumClass, false, false);
	}

	/**
	 * Read an option (blocking)
	 * The enum names will be converted by the `enumToOption` method and used as a prompt.
	 * The parameters will be used to chose which options are available.
	 * @param enumClass the class of the enum that implements `OptionsInterface` from which to chose the option
	 * @param isHost if the player is the host
	 * @param isTurn if it's the player's turn
	 * @return the read option
	 *
	 * @author Marco
	 */
	public <E extends Enum<E> & OptionsInterface> E askOption(Class<E> enumClass, boolean isHost, boolean isTurn) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			int ind = 0;
			System.out.println();
			for (E option : options) {
				if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
					continue;
				}
				ind++;
				System.out.printf("[%d] %s%n", ind, enumToOption(option));
			}
			int selected = askInt();

			// Because not all the options are selectable, we need to find it by iterating through them all again
			ind = 0;
			for (E option : options) {
				if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
					continue;
				}
				ind++;
				if (ind == selected) {
					return option;
				}
			}
			System.out.println("[!] Invalid option");
		}
	}

	/**
	 * Read an option or wait for an event
	 * The enum names will be converted by the `enumToOption` method and used as a prompt.
	 * The parameters will be used to chose which options are available.
	 * Since this function is designed to be called multiple time in the same way if an event arrives,
	 * there is an option not to print anything. It is up to the developer using this method, to ensure
	 * that the last printed options are the one from which the user can choose from.
	 * @param enumClass the class of the enum that implements `OptionsInterface` from which to chose the option
	 * @param doPrint if the options should be printed
	 * @param isHost if the player is the host
	 * @param isTurn if it's the player's turn
	 * @return the read option
	 *
	 * @author Marco
	 */
	public <E extends Enum<E> & OptionsInterface> Optional<E> askOptionOrEvent(Class<E> enumClass, boolean doPrint, boolean isHost, boolean isTurn) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			int ind;
			if (doPrint) {
				ind = 0;
				System.out.println();
				for (E option : options) {
					if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
						continue;
					}
					ind++;
					System.out.printf("[%d] %s%n", ind, enumToOption(option));
				}
			}
			Optional<Integer> selected = askIntOrEvent();
			if (selected.isEmpty()) {
				return Optional.empty();
			}

			// Because not all the options are selectable, we need to find it by iterating through them all again
			ind = 0;
			for (E option : options) {
				if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
					continue;
				}
				ind++;
				if (ind == selected.get()) {
					return Optional.of(option);
				}
			}
			System.out.println("[!] Invalid option");
			doPrint = true;
		}
	}
}
