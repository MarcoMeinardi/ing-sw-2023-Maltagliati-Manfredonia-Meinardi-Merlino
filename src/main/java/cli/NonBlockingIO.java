package cli;

import java.util.Scanner;

public class NonBlockingIO extends Thread {
	private static Scanner scanner = new Scanner(System.in);

	private static NonBlockingIO instance = null;

	private boolean isAsking = false;
	private boolean isAvailable = false;
	private String result;

	private NonBlockingIO() {}
	public static NonBlockingIO getInstance() {
		if (instance == null) {
			try {
				instance = new NonBlockingIO();
				instance.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}


	public void run() {
		try {
			while (true) {
				synchronized (scanner) {
					while (!isAsking) {
						scanner.wait();
					}
					result = scanner.nextLine();
					isAsking = false;
					isAvailable = true;
					while (isAvailable) {
						scanner.wait();
					}
				}
			}
		} catch (Exception e) {}  // Interrupt thread to quit
	}

	public void ask() {
		synchronized (scanner) {
			if (isAvailable) {
				throw new RuntimeException("Ask before getting result");
			}
			isAsking = true;
			scanner.notifyAll();
		}
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public String getResult() {
		String line;
		synchronized (scanner) {
			if (!isAvailable) {
				throw new RuntimeException("Result is not available");
			}
			isAvailable = false;
			line = result;
			scanner.notifyAll();
		}
		return line;
	}
}
