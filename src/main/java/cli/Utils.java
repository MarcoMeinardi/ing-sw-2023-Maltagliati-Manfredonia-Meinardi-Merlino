package cli;

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
		IO.ask();
		try {
			while (!IO.isAvailable()) {
				if (NetworkManager.getInstance().hasEvent()) {
					return Optional.empty();
				}
				Thread.sleep(50);
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

	private static <E extends Enum<E>> String enumToOption(E e) {
		String repr = e.toString();
		return repr.substring(0, 1).toUpperCase() + repr.substring(1).toLowerCase().replace('_', ' ');
	}

	public static <E extends Enum<E>> E askOption(Class<E> enumClass) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			for (int i = 0; i < options.length; i++) {
				System.out.println(String.format("[%d] %s", i + 1, enumToOption(options[i])));
			}
			int selected = askInt() - 1;
			if (selected >= 0 && selected < options.length) {
				return options[selected];
			} else {
				System.out.println("[!] Invalid option");
			}
		}
	}
	public static <E extends Enum<E>> Optional<E> askOptionOrEvent(Class<E> enumClass, boolean doPrint) {
		while (true) {
			E[] options = enumClass.getEnumConstants();
			if (doPrint) {
				for (int i = 0; i < options.length; i++) {
					System.out.println(String.format("[%d] %s", i + 1, enumToOption(options[i])));
				}
			}
			Optional<Integer> option = askIntOrEvent();
			if (option.isEmpty()) {
				return Optional.empty();
			}
			int selected = option.get() - 1;
			if (selected >= 0 && selected < options.length) {
				return Optional.of(options[selected]);
			} else {
				System.out.println("[!] Invalid option");
			}
		}
	}
}
