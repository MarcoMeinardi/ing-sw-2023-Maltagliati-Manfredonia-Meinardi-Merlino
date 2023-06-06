package network;

import java.util.Optional;

public interface ClientManagerInterface{
    public static ClientManagerInterface getInstance() throws Exception{
        return null;
    }
    public boolean isClientConnected(String username);

    /**
     * Returns the client with the given username. If it is logged in
     * @param username the username of the client.
     * @return the client with the given username.
     */
    public Optional<ClientInterface> getClient(String username);
    public Optional<ClientInterface> getClientEvenIfDisconnected(String username);
    public void waitAndClose();

    public boolean isUsernameTaken(String username);
}
