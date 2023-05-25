package view.cli;

import java.util.Scanner;

import network.NetworkManagerInterface;

public class NonBlockingIO extends Thread {
	private static Scanner scanner = new Scanner(System.in);

	private static NonBlockingIO instance = null;

	private NetworkManagerInterface networkManager = null;

	private boolean isAsking = false;
	private boolean isAvailable = false;
	private String result;

	private final Object isAskingLock = new Object();
	private final Object isAvailableLock = new Object();

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

	public void setNetworkManager(NetworkManagerInterface networkManager) {
		this.networkManager = networkManager;
	}

	public void run() {
		try {
			while (true) {
				synchronized (isAskingLock) {
					while (!isAsking) {
						isAskingLock.wait();
					}
				}
				result = scanner.nextLine();
				synchronized (isAvailableLock) {
					isAvailable = true;
					if (networkManager != null) {
						synchronized (networkManager) {
							networkManager.notifyAll();
						}
					}
					while (isAvailable) {
						isAvailableLock.wait();
					}
				}
			}
		} catch (Exception e) {}  // Interrupt thread to quit
	}

	public void ask() {
		synchronized (isAskingLock) {
			if (isAvailable) {
				throw new RuntimeException("Ask before getting result");
			}
			isAsking = true;
			isAskingLock.notifyAll();
		}
	}

	public boolean isAvailable() {
		synchronized (isAvailableLock) {
			return isAvailable;
		}
	}

	public String getResult() {
		String line;
		synchronized (isAskingLock) {
			synchronized (isAvailableLock) {
				if (!isAvailable) {
					throw new RuntimeException("Result is not available");
				}
				isAvailable = false;
				isAsking = false;
				line = result;
				isAvailableLock.notifyAll();
			}
		}
		return line;
	}
}
