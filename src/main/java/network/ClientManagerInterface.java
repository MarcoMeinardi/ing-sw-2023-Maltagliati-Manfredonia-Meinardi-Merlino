package network;

import java.util.Optional;

/**
 * Interface for the ClientManager object used in the server to handle a group of clients that use the same technology
 */
public interface ClientManagerInterface{

    /**
     * Get the instance of the ClientManager
     * @return the instance of the ClientManager
     * @throws Exception if the instance cannot be created
     */
    public static ClientManagerInterface getInstance() throws Exception{
        throw new Exception("Not implemented");
    }

    /**
     * Check if a client is connected to the client manager
     * @param username the username of the client
     * @return true if the client is connected, false otherwise
     */
    public boolean isClientConnected(String username);

    /**
     * Returns the client with the given username. If it is logged in
     * @param username the username of the client.
     * @return the client with the given username.
     */
    public Optional<ClientInterface> getClient(String username);

    /**
     * Wait for all the inner threads to finish and close the client manager
     */
    public void waitAndClose();

    /**
     * Check if a username is already taken in this client manager
     * @param username the username to check
     * @return true if the username is already taken, false otherwise
     */
    public boolean isUsernameTaken(String username);
}
