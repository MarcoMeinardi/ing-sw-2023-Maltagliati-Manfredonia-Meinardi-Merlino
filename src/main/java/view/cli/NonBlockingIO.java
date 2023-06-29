package view.cli;

import java.util.Scanner;

import network.NetworkManagerInterface;

/**
 * Singleton class to handle non-blocking IO for the CLI.
 * **DON'T** use this class directly from the CLI, use the `Utils` class.
 * For the developers of `Utils`: to use this class, get an instance
 * `NonBlockingIO IO = NonBlockingIO.getInstance()` and once you get the network manager,
 * `IO.setNetworkManager(networkManager)`.
 * To ask for a string, call `IO.ask()` and wait until `IO.isAvailable()` is not true,
 * once this happened, you can get the result **only once** with `IO.getResult()`.
 * Note that you have to call `getResult` before calling `ask` a second time.
 * If the network manager is set, you'll wait on it, and you'll get notified either
 * by this class if a string has been read, or directly by the network manager if an event occurred.
 *
 * @author Marco
 */
public class NonBlockingIO extends Thread {
	private static Scanner scanner = new Scanner(System.in);

	private static NonBlockingIO instance = null;

	private NetworkManagerInterface networkManager = null;

	private boolean isAsking = false;
	private boolean isAvailable = false;
	private String result;

	private final Object isAskingLock = new Object();
	private final Object isAvailableLock = new Object();

	/**
	 * Class constructor
	 *
	 * @author Marco
	 */
	private NonBlockingIO() {}

	/**
	 * Return the instance of the `NonBlockingIO` singleton
	 * @return the `NonBlockingIO` instance
	 *
	 * @author Marco
	 */
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

	/**
	 * Set the network manager
	 * @param networkManager the network manager instance
	 *
	 * @author Marco
	 */
	public void setNetworkManager(NetworkManagerInterface networkManager) {
		this.networkManager = networkManager;
	}

	/**
	 * Thread's run function to handle the non-blocking IO and server events
	 *
	 * @author Marco
	 */
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

	/**
	 * Set the ask flag to true, this allows a string to be read
	 * You cannot call this method twice, without getting the result of
	 * the previous read.
	 * @throws RuntimeException if the method is called twice without waiting for the result.
	 *
	 * @author Marco
	 */
	public void ask() {
		synchronized (isAskingLock) {
			if (isAvailable) {
				throw new RuntimeException("Ask before getting result");
			}
			isAsking = true;
			isAskingLock.notifyAll();
		}
	}

	/**
	 * Check if a string has been read
	 *
	 * @author Marco
	 */
	public boolean isAvailable() {
		synchronized (isAvailableLock) {
			return isAvailable;
		}
	}

	/**
	 * Get the read string
	 * @return the read string
	 * @throws RuntimeException if the result is not available (you have to check `isAvailable()` before calling this function)
	 *
	 * @author Marco
	 */
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
