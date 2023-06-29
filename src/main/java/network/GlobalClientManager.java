package network;


import java.util.Optional;

/**
 * Singleton class that holds the client manager for both RMI and RPC
 */
public class GlobalClientManager implements ClientManagerInterface{
    private static GlobalClientManager instance = null;
    private final ClientManagerInterface rmiClientManager;
    private final ClientManagerInterface rpcClientManager;

    /**
     * Private constructor to prevent instantiation
     * @throws Exception if there is any kind of problem during the login process
     */
    private GlobalClientManager() throws Exception {
        rmiClientManager = network.rmi.server.ClientManager.getInstance();
        rpcClientManager = network.rpc.server.ClientManager.getInstance();
    }

    /**
     * Get the singleton instance of the GlobalClientManager
     * @return the singleton instance of the GlobalClientManager
     * @throws Exception if there is any kind of problem during the login process
     */
    public static ClientManagerInterface getInstance() throws Exception {
        if(instance == null){
            instance = new GlobalClientManager();
        }
        return instance;
    }

    /**
     * Check if the client is connected
     * @param username the username of the client
     * @return true if the client is connected, false otherwise
     */
    @Override
    public boolean isClientConnected(String username) {
        return false;
    }

    /**
     * Method that first tries to obtain the client from the RPC client manager,
     * and if it is not found, falls back to the RMI client manager.
     * @param username the username of the client to retrieve
     * @return an Optional containing the client if found, or an empty Optional if not found
     */
    @Override
    public Optional<ClientInterface> getClient(String username) {
        return rpcClientManager.getClient(username).or(() -> rmiClientManager.getClient(username));
    }

    /**
     * Waits for any ongoing operations to complete and closes the RPC and RMI client managers.
     */
    @Override
    public void waitAndClose() {
        rpcClientManager.waitAndClose();
        rmiClientManager.waitAndClose();
    }

    /**
     * Checks if the username is already taken by another client
     * @param username the username to check
     * @return true if the username is already taken, false otherwise
     */
    @Override
    public boolean isUsernameTaken(String username) {
        return rpcClientManager.isUsernameTaken(username) || rmiClientManager.isUsernameTaken(username);
    }
}
