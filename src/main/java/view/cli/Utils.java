package view.cli;

import java.util.Optional;

import network.rpc.client.NetworkManager;

public class Utils {
	private static NonBlockingIO IO = NonBlockingIO.getInstance();

	public static String askString() {
		IO.ask();
		try {
			while (!IO.isAvailable()) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			return null;
		}
		return IO.getResult();
	}
	public static String askString(String message) {
		System.out.print(message);
		return askString();
	}
	public static Optional<String> askStringOrEvent() {
		if (NetworkManager.getInstance().hasEvent()) {
			return Optional.empty();
		}
		IO.ask();
		NetworkManager networkManager = NetworkManager.getInstance();
		try {
			synchronized (networkManager) {
				while (!IO.isAvailable() && !networkManager.hasEvent()) {
					networkManager.wait();
				}
				if (!IO.isAvailable() && NetworkManager.getInstance().hasEvent()) {
					return Optional.empty();
				}
			}
		} catch (InterruptedException e) {
			return Optional.empty();
		}
		return Optional.of(IO.getResult());
	}
	public static Optional<String> askStringOrEvent(String message) {
		System.out.print(message);
		return askStringOrEvent();
	}

	public static int askInt() {
		int result = -1;
		String line = askString();
		try {
			result = Integer.parseInt(line);
		} catch (Exception e) {}
		return result;
	}
	public static int askInt(String message) {
		System.out.print(message);
		return askInt();
	}
	public static Optional<Integer> askIntOrEvent() {
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
	public static Optional<Integer> askIntOrEvent(String message) {
		System.out.print(message);
		return askIntOrEvent();
	}

	private static <E extends Enum<E> & OptionsInterface> String enumToOption(E e) {
		String repr = e.toString();
		return repr.substring(0, 1).toUpperCase() + repr.substring(1).toLowerCase().replace('_', ' ');
	}

	public static <E extends Enum<E> & OptionsInterface> E askOption(Class<E> enumClass) {
		return askOption(enumClass, false, false);
	}
	public static <E extends Enum<E> & OptionsInterface> E askOption(Class<E> enumClass, boolean isHost, boolean isTurn) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			int ind = 0;
			for (E option : options) {
				if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
					continue;
				}
				ind++;
				System.out.println(String.format("[%d] %s", ind, enumToOption(option)));
			}
			int selected = askInt();

			// Because not all the options are choosable, we need to find it by iterating through them all again
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
	public static <E extends Enum<E> & OptionsInterface> Optional<E> askOptionOrEvent(Class<E> enumClass, boolean doPrint, boolean isHost, boolean isTurn) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			int ind;
			if (doPrint) {
				ind = 0;
				for (E option : options) {
					if ((option.needHost() && !isHost) || (option.needTurn() && !isTurn)) {
						continue;
					}
					ind++;
					System.out.println(String.format("[%d] %s", ind, enumToOption(option)));
				}
			}
			Optional<Integer> selected = askIntOrEvent();
			if (selected.isEmpty()) {
				return Optional.empty();
			}

			// Because not all the options are choosable, we need to find it by iterating through them all again
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
