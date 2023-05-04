package network;

import java.util.Optional;

public interface ClientManagerInterface{
    public static ClientManagerInterface getInstance(){
        return null;
    }
    public boolean isClientConnected(String username);
    public Optional<ClientInterface> getClient(String username);
    public void waitAndClose();
}
