package network.rmi.server;

import controller.game.GameController;
import controller.lobby.LobbyController;
import model.Player;
import network.*;
import network.errors.ClientAlreadyConnectedExeption;
import network.parameters.GameInfo;
import network.parameters.Login;
import network.rmi.LoginService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class ClientManager extends Thread implements ClientManagerInterface, LoginService {
    HashMap <String, Client> clients;
    public static int port = 8001;
    private int availablePort = 10000;
    private final Object availablePortLock;
    private final Registry registry;
    private final LoginService stub;
    private static ClientManager instance = null;
    private static final Object instanceLock = new Object();
    private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

    private ClientManager() throws Exception{
        clients = new HashMap<>();
        availablePortLock = new Object();
        stub = (LoginService) UnicastRemoteObject.exportObject(this, availablePort);
        availablePort++;
        registry = LocateRegistry.createRegistry(port);
        registry.rebind("LoginService", stub);
    }

    public static ClientManagerInterface getInstance() throws Exception{
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new ClientManager();
                instance.start();
            }
            return instance;
        }
    }
    @Override
    public boolean isClientConnected(String username) {
        return clients.containsKey(username) && !clients.get(username).isDisconnected();
    }

    @Override
    public Optional<ClientInterface> getClient(String username) {
        Optional<ClientInterface> client = Optional.empty();
        if(clients.containsKey(username)){
            client = Optional.of(clients.get(username));
        }
        return client;
    }

    @Override
    public void waitAndClose() {
        try{
            this.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public Result<Serializable> login(Login info, UUID callId) throws Exception {
        String username = info.username();
        boolean wasConnected = false;
        if(GlobalClientManager.getInstance().isUsernameTaken(username)){
            if(!clients.containsKey(username) || !clients.get(username).isDisconnected()){
                return Result.err(new ClientAlreadyConnectedExeption(),callId);
            }
            wasConnected = true;
        }
        Optional<GameController> game = LobbyController.getInstance().searchGame(username);
        if(wasConnected && game.isPresent()){
            GameController gameController = game.get();
            Player player = gameController.getPlayer(username);
            return Result.ok(game.get().getGameInfo(player), callId);
        }else {
            synchronized (availablePortLock) {
                Client client = new Client(username, registry, availablePort);
                clients.put(username, client);
                availablePort++;
            }
        }
        return Result.empty(callId);
    }

    @Override
    public void run(){
        boolean running;
        synchronized (instanceLock){
            running = instance != null;
        }
        while(running){
            try{
                Thread.sleep((Client.TIMEOUT/2)*1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            for(Client client : clients.values()){
                if(!client.getStatus().equals(ClientStatus.Disconnected) && !client.checkPing()){
                    logger.info("Client " + client.username + " timed out");
                    client.setStatus(ClientStatus.Disconnected);
                }
            }
            synchronized (instanceLock){
                running = instance != null;
            }
        }
    }

    @Override
    public boolean isUsernameTaken(String username) {
        synchronized (clients){
            return clients.containsKey(username);
        }
    }
}
