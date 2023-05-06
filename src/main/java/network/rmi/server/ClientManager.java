package network.rmi.server;

import network.ClientInterface;
import network.ClientManagerInterface;
import network.rmi.LoginService;

import java.rmi.RemoteException;
import java.util.Optional;

public class ClientManager implements ClientManagerInterface, LoginService {
    @Override
    public boolean isClientConnected(String username) {
        return false;
    }

    @Override
    public Optional<ClientInterface> getClient(String username) {
        return Optional.empty();
    }

    @Override
    public void waitAndClose() {

    }

    @Override
    public boolean login(String username) throws RemoteException {
        return false;
    }
}
