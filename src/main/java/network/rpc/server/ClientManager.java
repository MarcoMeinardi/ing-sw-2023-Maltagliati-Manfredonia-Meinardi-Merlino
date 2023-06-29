package network.rpc.server;

import controller.lobby.LobbyController;
import controller.game.GameController;
import network.*;
import network.errors.ClientAlreadyConnectedException;
import network.errors.ClientNotIdentifiedException;
import network.errors.InvalidUsernameException;
import network.parameters.Login;
import network.errors.WrongParametersException;

import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import static network.Server.SERVER_NAME;

public class ClientManager extends Thread implements ClientManagerInterface{
    final private LinkedList<Client> unidentifiedClients = new LinkedList<>();
    final private HashMap<String, Client> identifiedClients = new HashMap<>();
    private ServerSocket socket;
    private Thread acceptConnectionsThread;

    private static int port = 8000;

    private static ClientManager instance = null;
    private static final Object instanceLock = new Object();

    public static void setPort(int port){
        ClientManager.port = port;
    }


    /**
     * Method that retrieves the instance of the ClientManagerInterface.
     * If the instance is null, a new instance of ClientManager is created with the specified port and started.
     * The retrieval and creation of the instance are synchronized on the instanceLock object to ensure thread safety.
     *
     * @return The instance of the ClientManagerInterface.
     * @throws Exception If an error occurs during the creation or start of the ClientManager instance.
     *
     * @author Ludovico
     */
    public static ClientManagerInterface getInstance() throws Exception {
        synchronized (instanceLock){
            if(instance == null){
                instance = new ClientManager(port);
                instance.start();
            }
            return instance;
        }
    }

    /**
     * Constructor that creates a ClientManager object with the given port.
     * It initializes a ServerSocket using the specified port to listen for incoming connections.
     * It also creates a new thread that will handle accepting connections by invoking the acceptConnections method.
     */
    private ClientManager(int port) throws Exception {
        this.socket = new ServerSocket(port);
        this.acceptConnectionsThread = new Thread(this::acceptConnections);
    }

    /**
     * Registers the service requested by the call and returns the corresponding result.
     *
     * Mathod that registers the service requested by the call and returns the corresponding result.
     * - If the service of the call is not Service.Login, it returns an error Result indicating that the client is not identified.
     * - If the parameters of the call do not match the expected Login type, it returns an error Result indicating wrong parameters.
     * - If the length of the login username exceeds 16 characters or equals the SERVER_NAME constant, it returns an error Result indicating an invalid username.
     * - It tries to add the identified client using the login username and the client instance. If the addition is successful:
     *   - It searches for a game associated with the login username using the LobbyController.
     *   - If a game is found, it returns a Result with the game information for the player.
     *   - If no game is found, it returns an empty Result.
     * - If the addition of the identified client fails, it returns an empty Result.
     * - If an exception occurs during the process, it returns an error Result with the corresponding exception.
     *
     * @param call   The Call object representing the requested service.
     * @param client The ClientInterface representing the client making the request.
     * @return The Result object representing the outcome of the registration process.
     *
     * @author Lorenzo,Marco
     */
    private Result<Serializable> registerService(Call<Serializable> call, ClientInterface client) {
        if(call.service() != Service.Login) {
            return Result.err(new ClientNotIdentifiedException(), call.id());
        }
        if(!(call.params() instanceof Login)) {
            return Result.err(new WrongParametersException("Login",call.params().getClass().getName(),"call.param()"), call.id());
        }
        Login login = (Login)call.params();
        if (login.username().length() > 16 || login.username().equals(SERVER_NAME)) {
            return Result.err(new InvalidUsernameException(), call.id());
        }
        try{
            if (addIdentifiedClient(login.username(), (Client) client)) {
                Optional<GameController> game = LobbyController.getInstance().searchGame(login.username());
                if (game.isPresent()) {
                    return Result.ok(game.get().getGameInfo(game.get().getPlayer(login.username())), call.id());
                } else {
                    return Result.empty(call.id());
                }
            } else {
                return Result.empty(call.id());
            }
        }catch (Exception e){
            return Result.err(e, call.id());
        }
    }

    /**
     * Method that continuously accepts incoming connections and handles them. It runs in a loop until the instance of
     * ClientManager is not null. Within each iteration:
     * - Accepts a new client connection by invoking the accept method on the ServerSocket.
     * - Creates a new Client object with the accepted socket and the registerService method as the service registration handler.
     * - Adds the newly created client as an unidentified client.
     * - Starts the client by invoking its start method.
     * - If an exception occurs during the process, it logs a warning message using the Logger class.
     *
     * @author Lorenzo
     */
    private void acceptConnections(){
        while(instance != null){
            try{
                Client client = new Client(socket.accept(), this::registerService);
                addUnidentifiedClient(client);
                client.start();
            }catch (Exception e){
                Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            }
        }
    }

    /**
     * Method that adds the specified client to the collection of unidentified clients.
     * It ensures thread safety by synchronizing access to the collection using the 'unidentifiedClients' object as the lock.
     * The client is added to the collection for further processing or identification.
     *
     * @param client The Client object to be added as an unidentified client.
     *
     * @author Marco
     */
    private void addUnidentifiedClient(Client client){
        synchronized (unidentifiedClients) {
            unidentifiedClients.add(client);
        }
    }

    /**
     * Adds an identified client to the collection of identified clients.
     *
     * This method adds the specified client to the collection of identified clients using the given username.
     * - Checks if the username is already taken by another client using the GlobalClientManager. If the username is taken and the corresponding client is not disconnected, it throws a ClientAlreadyConnectedException.
     * - If the username is already present in the identifiedClients collection, it sets the 'wasConnected' flag to true, disconnects the existing client associated with the username, and transfers data from the existing client to the new client.
     * - If the username is not present in the identifiedClients collection, it sets the call handler for the client using the handleLobbySearch method of the LobbyController.
     * - Sets the username for the client.
     * - Adds the client to the identifiedClients collection and removes it from the unidentifiedClients collection.
     *
     * @param username The username associated with the client.
     * @param client   The Client object to be added as an identified client.
     * @return True if the username was already connected to another client, false otherwise.
     * @throws ClientAlreadyConnectedException If the username is already taken by another client that is not disconnected.
     * @throws Exception                      If an error occurs during the process.
     *
     * @author Lorenzo, Marco
     */
    private boolean addIdentifiedClient(String username, Client client) throws Exception {
        boolean wasConnected = false;
        synchronized (identifiedClients) {
            ClientManagerInterface globalManager = GlobalClientManager.getInstance();
            if(globalManager.isUsernameTaken(username)){
                if(!identifiedClients.containsKey(username) || !identifiedClients.get(username).isDisconnected()) {
                    throw new ClientAlreadyConnectedException();
                }
            }
            if(identifiedClients.containsKey(username)){
                wasConnected = true;
                identifiedClients.get(username).disconnect();
                Client lastClient = identifiedClients.get(username);
                (client).from(lastClient);
            }else{
                client.setCallHandler(LobbyController.getInstance()::handleLobbySearch);
            }
            (client).setUsername(username);
            identifiedClients.put(username, client);
            unidentifiedClients.remove(client);
        }

        return wasConnected;
    }

    /**
     * Executes the main logic of the ClientManager.
     *
     * Method that represents the main logic of the ClientManager. It starts the acceptConnectionsThread and then enters a loop
     * to continuously check the status of identified clients and perform necessary operations.
     * Within each iteration of the loop, it performs the following steps:
     * - Checks if the instance of the ClientManager is still running.
     * - Iterates over the identifiedClients collection and checks the status of each client.
     * - If a client's status is not disconnected, it checks the client's ping. If the ping check fails, the client is interrupted.
     * - Sleeps for a duration specified by Client.TIMEOUT to avoid excessive processing.
     * - Checks the running status again.
     * - If interrupted by an InterruptedException, it logs a warning message using the Logger class and interrupts the acceptConnectionsThread.
     *
     * @author Lorenzo
     */
    public void run(){
        acceptConnectionsThread.start();
        try{
            boolean running;
            synchronized (instanceLock){
                running = instance != null;
            }
            while(running){
                synchronized (identifiedClients) {
                    for (Client client : identifiedClients.values()) {
                        if(client.getStatus() != ClientStatus.Disconnected){
                            if(!client.checkPing()) {
                                client.interrupt();
                            }
                        }
                    }
                }
                Thread.sleep(Client.TIMEOUT * 1000 / 2);
                synchronized (instanceLock){
                    running = instance != null;
                }
            }
        }catch(InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            acceptConnectionsThread.interrupt();
        }
    }

    /**
     * Method that attempts to retrieve the client with the given username from the identifiedClients collection.
     * - Initializes an empty Optional<ClientInterface> object to hold the result.
     * - Synchronizes access to the identifiedClients collection using the 'identifiedClients' object as the lock.
     * - Checks if the identifiedClients collection contains the specified username.
     * - If the username is found, it creates an Optional object with the corresponding client and assigns it to the 'client' variable.
     * - Returns the 'client' Optional object, which will be empty if the username is not found in the identifiedClients collection.
     *
     * @param username The username associated with the client.
     * @return An Optional containing the client with the specified username, or an empty Optional if the username is not found.
     *
     * @author Lorenzo
     */
    @Override
    public Optional<ClientInterface> getClient(String username){
        Optional<ClientInterface> client = Optional.empty();
        synchronized (identifiedClients) {
            if(identifiedClients.containsKey(username)){
                client = Optional.of(identifiedClients.get(username));
            }
        }
        return client;
    }

    /**
     * Method that waits for the acceptConnectionsThread to complete by invoking the 'join' method on the acceptConnectionsThread object.
     * It also closes the socket associated with the ClientManager.
     * Additionally, it waits for the current thread (ClientManager) to complete by invoking the 'join' method on itself.
     * Furthermore, it synchronizes access to the unidentifiedClients and identifiedClients collections and disconnects all clients in these collections.
     *
     * @author Lorenzo
     */
    @Override
    public void waitAndClose() {
        try{
            acceptConnectionsThread.join();
        }catch (InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
        }
        try{
            socket.close();
        }catch (Exception e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
        }
        try{
            this.join();
        }catch (InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
        }
        synchronized (unidentifiedClients) {
            for (Client client : unidentifiedClients) {
                client.disconnect();
            }
        }
        synchronized (identifiedClients) {
            for (Client client : identifiedClients.values()) {
                client.disconnect();
            }
        }
    }

    /**
     * Checks if a client with the specified username is currently connected.
     *
     * This method checks if the identifiedClients collection contains the specified username and if the corresponding client's status is not set to "Disconnected".
     * - Synchronizes access to the identifiedClients collection using the 'identifiedClients' object as the lock.
     * - Checks if the identifiedClients collection contains the specified username.
     * - If the username is found, it retrieves the corresponding client and checks if its status is not set to "Disconnected".
     * - Returns true if the username is found and the client is connected, false otherwise.
     *
     * @param username The username associated with the client.
     * @return true if a client with the specified username is currently connected, false otherwise.
     *
     * @author Ludovico
     */
    public boolean isClientConnected(String username){
        synchronized (identifiedClients) {
            return identifiedClients.containsKey(username) && identifiedClients.get(username).getStatus() != ClientStatus.Disconnected;
        }
    }

    /**
     * Checks if a username is already taken by an identified client.
     *
     * This method checks if the identifiedClients collection contains the specified username.
     * - Synchronizes access to the identifiedClients collection using the 'identifiedClients' object as the lock.
     * - Checks if the identifiedClients collection contains the specified username.
     * - Returns true if the username is found in the identifiedClients collection, false otherwise.
     *
     * @param username The username to check.
     * @return true if the username is already taken by an identified client, false otherwise.
     *
     * @author Lorenzo
     */
    public boolean isUsernameTaken(String username){
        synchronized (identifiedClients) {
            return identifiedClients.containsKey(username);
        }
    }
}
