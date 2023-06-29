package network.rpc.client;

import controller.lobby.Lobby;
import network.*;
import network.parameters.CardSelect;
import network.parameters.LobbyCreateInfo;
import network.parameters.Login;
import network.parameters.Message;

import java.io.Serializable;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class that handles the netowrking server-client, server-side
 */
public class NetworkManager implements NetworkManagerInterface {
    static private final int socketTimeout = 60000;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Boolean connected = false;
    private final Object connectedLock = new Object();
    private Server server;
    private static NetworkManager instance;
    private final HashMap<UUID, Function> callQueue = new HashMap<>();
    private final Queue<ServerEvent> eventQueue = new LinkedList<>();
    private final Logger logger = Logger.getLogger(NetworkManager.class.getName());
    private Function<LocalDateTime,Boolean> lastPing = null;
    private static final int PING_TIMEOUT = 1;
    private Thread checkPingThread;
    private Thread mainThread;
    private NetworkManager(){}

    /**
     * Get the singleton instance of the network manager
     * @return the singleton instance of the network manager
     *
     * @author Lorenzo
     */
    public static NetworkManager getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }

    /**
     * Method that takes a `Server` object as a parameter and establishes a connection to the server.
     * It sets the `connected` flag to `false`, creates a new `Socket` object using the server's IP address and port number,
     * and initializes the input and output streams for the socket.
     * It also sets a socket timeout and tests the connection.
     * If the connection is successful, it sets the `connected` flag to `true`, starts a new thread to run the `run` method, and logs a message indicating that the connection was successful.
     * If any exceptions occur during the connection process, they are thrown as an `Exception`.
     * @param server the server to connect to
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public void connect(Server server) throws Exception{
        setConnected(false);
        this.server = server;
        this.socket = new Socket(server.ip(), server.port());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.socket.setSoTimeout(socketTimeout);
        testConnection();
        setConnected(true);
        mainThread = new Thread(this::run);
        mainThread.start();
        logger.info("Connected to server");
    }

    /**
     * Method named `disconnect` that is synchronized, only one thread can execute it at a time.
     * It checks if the connection is currently established, sets the `connected` flag to false, logs a message, adds a `ServerDisconnect` event to the `eventQueue`,
     * clears the `callQueue`, and notifies all threads waiting on the `instance` object.
     * Finally, it attempts to close the socket.
     * This method is used to cleanly disconnect from a server and clean up any remaining resources.
     *
     * @author Lorenzo
     */
    @Override
    synchronized public void disconnect(){
        if(isConnected()){
            setConnected(false);
            logger.info("Disconnecting from server");
            synchronized (eventQueue){
                eventQueue.add(ServerEvent.ServerDisconnect());
            }
            synchronized(callQueue){
                callQueue.clear();
            }
            synchronized (instance){
                instance.notifyAll();
            }
            try{
                this.socket.close();
            }catch(Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }

    /**
     * Wait a result from the server
     * @return the result
     * @throws Exception if an error occurs while receiving the result
     *
     * @author Lorenzo
     */
    private Result<Serializable> receive() throws Exception{
        synchronized(in){
            Object obj = in.readObject();
            if(!(obj instanceof Result)){
                throw new Exception("Invalid object received");
            }
            return (Result<Serializable>)obj;
        }
    }

    /**
     * Test the connection to the server
     * @throws Exception if an error occurs while testing the connection
     *
     * @author Lorenzo
     */
    private void testConnection() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        lastPing = new Function<>(now, Service.Ping);
        lastPing.call(out);
        Object obj = in.readObject();
        if(!(obj instanceof Result)){
            throw new Exception("Invalid object received");
        }
        lastPing.setResult((Result<Boolean>)obj);
    }

    /**
     * Check if the client is connected to the server and update the lastPing
     *
     * @author Lorenzo
     */
    private void checkPing() {
        while (isConnected()) {
            try {
                if (lastPing.checkResult().isPresent()) {
                    lastPing.call(out);
                }
            } catch (Exception e) {
                logger.warning(e.getMessage());
                disconnect();
            }
            try {
                Thread.sleep(PING_TIMEOUT*1000);
            } catch (Exception e) {
                logger.warning(e.getMessage());
                disconnect();
            }
        }
    }

    /**
     * Method that is responsible for receiving and processing messages from a server while the client is connected.
     * It runs in a loop while the `connected` flag is set to `true`.
     * It receives a `Result` object from the server, and if it is an event, it adds it to the `eventQueue` and notifies all threads waiting on the `instance` object.
     * If it is a response to a previous call, it sets the result of the `Function` object that was previously added to the `callQueue`.
     * If it is a response to a ping, it updates the `lastPing` object.
     * If it is a response to a `ServerDisconnect` event, it disconnects from the server.
     *
     * @author Lorenzo, Marco
     *
     */
    public void run() {
        Result result;
        checkPingThread = new Thread(this::checkPing);
        checkPingThread.start();
        while (isConnected()) {
            try {
                result = receive();
            } catch (Exception e) {
                logger.warning(e.getMessage());
                disconnect();
                break;
            }
            try {
                if (result.isEvent()) {
                    ServerEvent event = (ServerEvent)result.unwrap();
                    synchronized (eventQueue) {
                        eventQueue.add(event);
                    }
                    synchronized (instance) {
                        instance.notifyAll();
                    }
                } else if (lastPing.id().equals(result.id())) {
                    synchronized (lastPing) {
                        lastPing.setResult((Result<Boolean>)result);
                    }
                } else {
                    Function caller;
                    synchronized (callQueue) {
                        caller = callQueue.get(result.id());
                    }
                    caller.setResult(result);
                }
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
        try {
            checkPingThread.interrupt();
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Reconnect to the server
     * @throws Exception if an error occurs while reconnecting
     *
     * @author Lorenzo
     */
    @Override
    public void reconnect() throws Exception{
        disconnect();
        connect(server);
    }

    /**
     * Set the connection status
     * @param connected the connection status
     *
     * @author Lorenzo
     */
    private void setConnected(boolean connected){
        synchronized (connectedLock){
            this.connected = connected;
        }
    }

    /**
     * Check if the client is connected to the server
     * @return true if the client is connected to the server, false otherwise
     *
     * @author Lorenzo
     */
    @Override
    public boolean isConnected(){
        synchronized (connectedLock){
            return connected;
        }
    }

    /**
     * Method that retrieves an event from a synchronized event queue.
     * It returns an Optional object that may contain a ServerEvent object if the queue is not empty, or an empty Optional object if the queue is empty.
     * The synchronized block ensures that only one thread can access the queue at a time, preventing race conditions and other concurrency issues.
     * If an event is retrieved from the queue, it is removed from the queue using the remove() method.
     * @return
     */
    @Override
    public Optional<ServerEvent> getEvent(){
        synchronized (eventQueue){
            if(eventQueue.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(eventQueue.remove());
        }
    }

    /**
     * Method that returns a boolean value indicating whether the `eventQueue` is empty or not.
     * @return true if the `eventQueue` is empty, false otherwise.
     *
     * @author Marco
     */
    @Override
    public boolean hasEvent() {
        synchronized (eventQueue){
            return !eventQueue.isEmpty();
        }
    }

    /**
     * Method that retrieves a list of lobbies from the service.
     * The lobby list is obtained by invoking
     * the "call" method of a Function instance with a Boolean value indicating the request for the lobby list.
     *
     * @return An ArrayList of lobbies.
     * @throws Exception If an error occurs while retrieving the lobby list.
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean,ArrayList<Lobby>> lobbyList() throws Exception{
        Function<Boolean,ArrayList<Lobby>> lobbyList = new Function<>(true, Service.LobbyList);
        callQueue.put(lobbyList.id(), lobbyList);
        lobbyList.call(out);
        return lobbyList;
    }

    /**
     * Method that creates a lobby using the provided LobbyCreateInfo.
     * This method creates a lobby based on the specified LobbyCreateInfo, which contains the necessary information
     * for the lobby creation process. The lobby creation operation is performed by invoking the "call" method of
     * a Function instance with the given LobbyCreateInfo as the input.
     * @param info The LobbyCreateInfo object containing the details required to create the lobby.
     * @return The created lobby as an instance of Lobby.
     * @throws Exception If an error occurs during the lobby creation process.
     *
     * @author Lorenzo
     */

    @Override
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo info) throws Exception {
        Function<LobbyCreateInfo, Lobby> lobbyCreate = new Function<>(info, Service.LobbyCreate);
        callQueue.put(lobbyCreate.id(), lobbyCreate);
        lobbyCreate.call(out);
        return lobbyCreate;
    }

    /**
     * Method that joins a lobby with the given lobbyName by invoking the "call" method of a Function instance
     * with the lobbyName as the input.
     * The lobby join operation allows the user to join an existing lobby identified by its name.
     *
     * @param lobbyName The name of the lobby to join.
     * @return The joined lobby as an instance of Lobby.
     * @throws Exception If an error occurs during the lobby join process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception {
        Function<String, Lobby> lobbyJoin = new Function<>(lobbyName, Service.LobbyJoin);
        callQueue.put(lobbyJoin.id(), lobbyJoin);
        lobbyJoin.call(out);
        return lobbyJoin;
    }

    /**
     * Method that leaves the current lobby by invoking the "call" method of a Function instance with a Boolean
     * value indicating the request to leave the lobby.
     * The lobby leave operation allows the user to leave the currently joined lobby.
     *
     * @return A Boolean value indicating the success of the lobby leave operation.
     * @throws Exception If an error occurs during the lobby leave process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, Boolean> lobbyLeave() throws Exception {
        Function<Boolean, Boolean> lobbyLeave = new Function<>(true, Service.LobbyLeave);
        callQueue.put(lobbyLeave.id(), lobbyLeave);
        lobbyLeave.call(out);
        return lobbyLeave;
    }

    /**
     * Method that updates the current lobby by invoking the "call" method of a Function instance with a Boolean
     * value indicating the request to update the lobby.
     * The lobby update operation allows the user to update the currently joined lobby.
     *
     * @return The updated lobby as an instance of Lobby.
     * @throws Exception If an error occurs during the lobby update process.
     *
     * @author Marco
     */
    @Override
    public Function<Boolean, Lobby> updateLobby() throws Exception {
        Function<Boolean, Lobby> lobbyUpdate = new Function<>(true, Service.LobbyUpdate);
        callQueue.put(lobbyUpdate.id(), lobbyUpdate);
        lobbyUpdate.call(out);
        return lobbyUpdate;
    }

    /**
     * Method that starts the game by invoking the "call" method of a Function instance with a Boolean
     * value indicating the request to start the game.
     * The game start operation allows the user to start the game.
     *
     * @return A Boolean value indicating the success of the game start operation.
     * @throws Exception If an error occurs during the game start process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean,Boolean> gameStart() throws Exception{
        Function<Boolean,Boolean> gameStart = new Function<>(true, Service.GameStart);
        callQueue.put(gameStart.id(), gameStart);
        gameStart.call(out);
        return gameStart;
    }

    /**
     * Method that loads the game by invoking the "call" method of a Function instance with a Boolean
     * value indicating the request to load the game.
     * The game load operation allows the user to load the game.
     *
     * @return A Boolean value indicating the success of the game load operation.
     * @throws Exception If an error occurs during the game load process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, Boolean> gameLoad() throws Exception {
        Function<Boolean,Boolean> gameLoad = new Function<>(true, Service.GameLoad);
        callQueue.put(gameLoad.id(), gameLoad);
        gameLoad.call(out);
        return gameLoad;
    }

    /**
     * Method that selects a card using the provided CardSelect object by invoking the "call" method of a Function
     * instance with the selected card as the input.
     * The card select operation allows the user to choose a card based on the provided CardSelect object.
     *
     * @param selected The CardSelect object representing the selected card.
     * @return A Boolean value indicating the success of the card select operation.
     * @throws Exception If an error occurs during the card select process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception{
        Function<CardSelect,Boolean> cardSelect = new Function<>(selected, Service.CardSelect);
        callQueue.put(cardSelect.id(), cardSelect);
        cardSelect.call(out);
        return cardSelect;
    }


    /**
     * Method that performs a login operation using the provided Login object, which contains the username for the login.
     * The login operation is executed by invoking the "call" method of a Function instance with the username as the input.
     *
     * @param username The Login object representing the username for the login operation.
     * @return A Serializable object representing the result of the login operation.
     * @throws Exception If an error occurs during the login process.
     *
     * @author Lorenzo
     */
    @Override
    public Function<Login, Serializable> login(Login username) throws Exception {
        Function<Login, Serializable> login = new Function<>(username, Service.Login);
        callQueue.put(login.id(), login);
        login.call(out);
        return login;
    }

    /**
     * Method that calls the `join()` method on the `mainThread` object, which waits for the thread to die.
     * This method is used to ensure that a thread has completed its execution before continuing with the rest of the program.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public void join() throws Exception {
        mainThread.join();
    }

    /**
     * Method that sends a chat message using the provided Message object, which encapsulates the content of the message.
     * The chat message is sent by invoking the "call" method of a Function instance with the message as the input.
     *
     * @param message The Message object representing the content of the chat message.
     * @return A Boolean value indicating the success of sending the chat message.
     * @throws Exception If an error occurs during the chat message sending process.
     *
     * @author Marco
     */
    @Override
    public Function<Message, Boolean> chat(Message message) throws Exception {
        Function<Message, Boolean> chat = new Function<>(message, Service.GameChatSend);
        callQueue.put(chat.id(), chat);
        chat.call(out);
        return chat;
    }

    /**
     * Method that initiates the exit from the current game session by invoking the "call" method of a Function
     * instance with a Boolean value indicating the request to exit the game.
     * The exit game operation allows the user to leave the current game session and return to the main menu or previous state.
     *
     * @return A Boolean value indicating the success of the exit game operation.
     * @throws Exception If an error occurs during the exit game process.
     *
     * @author Marco
     */
    @Override
    public Function<Boolean, Boolean> exitGame() throws Exception {
        Function<Boolean, Boolean> exitGame = new Function<>(true, Service.ExitGame);
        callQueue.put(exitGame.id(), exitGame);
        exitGame.call(out);
        return exitGame;
    }
}
