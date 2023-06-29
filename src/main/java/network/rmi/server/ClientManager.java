package network.rmi.server;

import controller.game.GameController;
import controller.lobby.LobbyController;
import model.Player;
import network.*;
import network.errors.ClientAlreadyConnectedException;
import network.errors.ClientConnectedButNotFoundException;
import network.errors.InvalidUsernameException;
import network.parameters.Login;
import network.rmi.LoginService;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static network.Server.SERVER_NAME;

/**
 * Class to manage the clients connected to the server via rmi.
 */
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

    /**
     * Constructor for the class.
     * @throws Exception if the registry cannot be created.
     */
    private ClientManager() throws Exception{
        clients = new HashMap<>();
        availablePortLock = new Object();
        stub = (LoginService) UnicastRemoteObject.exportObject(this, availablePort);
        availablePort++;

        registry = LocateRegistry.createRegistry(port);
        registry.rebind("LoginService", stub);
    }


    /**
     * Method to get the instance of the class.
     * @return the instance of the class.
     * @throws Exception if the instance cannot be created.
     */
    public static ClientManagerInterface getInstance() throws Exception{
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new ClientManager();
                instance.start();
            }
            return instance;
        }
    }

    /**
     * Check if a client is connected
     * @return true if the client is present and his status is not 'Disconnected`
     */
    @Override
    public boolean isClientConnected(String username) {
        return clients.containsKey(username) && !clients.get(username).isDisconnected();
    }

    /**
     * Get the client witht the given username
     * @return an optional containing the requested client or empty if not found
     */
    @Override
    public Optional<ClientInterface> getClient(String username) {
        Optional<ClientInterface> client = Optional.empty();
        if(clients.containsKey(username)){
            client = Optional.of(clients.get(username));
        }
        return client;
    }

    /**
     * Wait for the object thread to finish
     */
    @Override
    public void waitAndClose() {
        try{
            this.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Handle the login request
     * @param info the login parameters
     * @param callId the UUID of the call
     * @return the result of the call as a `Result` object
     * @throws Exception
     */
    @Override
    public Result<Serializable> login(Login info, UUID callId) throws Exception {
        String username = info.username();
        boolean wasConnected = false;
        if (username.length() > 16 || username.equals(SERVER_NAME)) {
            return Result.err(new InvalidUsernameException(), callId);
        }
        if(GlobalClientManager.getInstance().isUsernameTaken(username)){
            if(!clients.containsKey(username) || !clients.get(username).isDisconnected()){
                return Result.err(new ClientAlreadyConnectedException(),callId);
            }
            wasConnected = true;
        }
        Optional<GameController> game = LobbyController.getInstance().searchGame(username);
        if (wasConnected) {
            Client client = clients.get(username);
            if (client == null) {
                return Result.err(new ClientConnectedButNotFoundException(), callId);
            }
            client.recoverStatus();
            client.clearEventQueue();
            if (game.isPresent()) {
                GameController gameController = game.get();
                Player player = gameController.getPlayer(username);
                return Result.ok(game.get().getGameInfo(player), callId);
            }
        } else {
            synchronized (availablePortLock) {
                Client client = new Client(username, registry, availablePort);
                clients.put(username, client);
                availablePort++;
            }
        }
        return Result.empty(callId);
    }

    /**
     * Object thread run implementation
     * Check in a loop if a client is definitely unreachable, in that case, disconnect him
     */
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

    /**
     * Check if a given username is already used
     * @param username the wanted username
     * @return true if the username is already taken
     */
    @Override
    public boolean isUsernameTaken(String username) {
        synchronized (clients){
            return clients.containsKey(username);
        }
    }
}
