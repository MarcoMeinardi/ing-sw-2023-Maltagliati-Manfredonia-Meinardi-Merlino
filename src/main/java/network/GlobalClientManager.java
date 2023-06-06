package network;


import java.util.Optional;

public class GlobalClientManager implements ClientManagerInterface{
    private static GlobalClientManager instance = null;
    private final ClientManagerInterface rmiClientManager;
    private final ClientManagerInterface rpcClientManager;

    private GlobalClientManager() throws Exception {
        rmiClientManager = network.rmi.server.ClientManager.getInstance();
        rpcClientManager = network.rpc.server.ClientManager.getInstance();
    }

    public static ClientManagerInterface getInstance() throws Exception {
        if(instance == null){
            instance = new GlobalClientManager();
        }
        return instance;
    }
    @Override
    public boolean isClientConnected(String username) {
        return false;
    }

    @Override
    public Optional<ClientInterface> getClient(String username) {
        return rpcClientManager.getClient(username).or(() -> rmiClientManager.getClient(username));
    }

    @Override
    public void waitAndClose() {
        rpcClientManager.waitAndClose();
        rmiClientManager.waitAndClose();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return rpcClientManager.isUsernameTaken(username) || rmiClientManager.isUsernameTaken(username);
    }
}
