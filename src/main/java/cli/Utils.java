package cli;


public class Utils {
	private static NonBlockingIO IO = NonBlockingIO.getInstance();

	public static String askString(String message) {
		System.out.print(message);
		return askString();
	}
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

	public static int askInt(String message) {
		System.out.print(message);
		return askInt();
	}
	public static int askInt() {
		int result = -1;
		String line = askString();
		try {
			result = Integer.parseInt(line);
		} catch (Exception e) {}
		return result;
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
}
