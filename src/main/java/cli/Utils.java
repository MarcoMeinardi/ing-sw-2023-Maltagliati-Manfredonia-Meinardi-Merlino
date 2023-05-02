package cli;

import java.util.Scanner;

public class Utils {
	private static Scanner scanner = new Scanner(System.in);

	public static String askString(String message) {
		System.out.print(message);
		return scanner.nextLine();
	}
	public static String askString() {
		return scanner.nextLine();
	}

	public static int askInt(String message) {
		int result = -1;
		try {
			System.out.print(message);
			result = scanner.nextInt();
		} catch (Exception e) {}
		scanner.nextLine();
		return result;
	}
	public static int askInt() {
		int result = -1;
		try {
			result = scanner.nextInt();
		} catch (Exception e) {}
		scanner.nextLine();
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
			int selected = Utils.askInt() - 1;
			if (selected >= 0 && selected < options.length) {
				return options[selected];
			} else {
				System.out.println("[!] Invalid option");
			}
		}
	}
}
