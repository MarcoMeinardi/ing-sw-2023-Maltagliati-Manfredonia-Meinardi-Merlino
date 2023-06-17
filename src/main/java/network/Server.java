package network;

/**
 * Record to store the server ip and port
 * @param ip the server ip
 * @param port the server port
 */
public record Server(String ip, int port) {
	public static final String SERVER_NAME = "Server";
}
