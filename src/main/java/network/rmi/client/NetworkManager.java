package network.rmi.client;

import controller.lobby.Lobby;
import network.*;
import network.errors.ClientNeverConnectedException;
import network.errors.ClientNotIdentifiedException;
import network.parameters.CardSelect;
import network.parameters.LobbyCreateInfo;
import network.parameters.Login;
import network.parameters.Message;
import network.rmi.ClientService;
import network.rmi.LoginService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.logging.Logger;

public class NetworkManager implements NetworkManagerInterface {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static NetworkManager instance = null;
    private Registry registry;
    private Optional<ClientService> clientService;
    private LoginService loginService;
    private boolean connected;
    private final Object connectedLock = new Object();

    private LocalDateTime pingTime = LocalDateTime.now();
    private final Object lastMessageLock = new Object();
    private final int PING_TIMEOUT = 1;
    private Server serverInfo;
    private Thread mainThread;
    private final Queue<ServerEvent> eventQueue = new LinkedList<>();

    /**
     * Method to get the connected status of the client.
     * @throws Exception if the client is not connected.
     *
     * @author Lorenzo
     */
    @Override
    public void connect(Server server) throws Exception {
        registry = LocateRegistry.getRegistry(server.ip(), server.port());
        clientService = Optional.empty();
        loginService = (LoginService) registry.lookup("LoginService");
        setConnected(true);
        pingTime = LocalDateTime.now();
        serverInfo = server;
        mainThread = new Thread(this::run);
        mainThread.start();
    }

    /**
     * Set the connected status of the client.
     * @param connected the new status.
     *
     * @author Lorenzo
     */
    private void setConnected(boolean connected){
        synchronized (connectedLock){
            this.connected = connected;
        }
    }

    /**
     * Method to get the instance of the class.
     * @return the instance of the class.
     *
     * @author Lorenzo
     */
    public static NetworkManagerInterface getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }

    /**
     * Set ping time to now.
     *
     * @author Lorenzo
     */
    private void setLastMessage(){
        synchronized (lastMessageLock){
            this.pingTime = LocalDateTime.now();
        }
    }

    /**
     * Get the elapsed time since the last message.
     * @return the elapsed time in seconds.
     *
     * @author Lorenzo
     */
    private long getElapsedTimeSinceLastMessage(){
        synchronized (lastMessageLock){
            return Duration.between(pingTime, LocalDateTime.now()).getSeconds();
        }
    }

    /**
     * Method that contains a loop that runs as long as the connection is still active.
     * Within the loop, it checks if a `clientService` is present
     * and sends a ping request if a certain amount of time has elapsed since the last message.
     * It also checks for any server events and adds them to an `eventQueue`.
     * Finally, it notifies the `NetworkManager` instance that an event has occurred.
     * If an exception is caught, it disconnects from the server.
     *
     * @author Lorenzo
     */
    public void run(){
        while(isConnected()){
            try{
                if(clientService.isPresent()) {
                    if (getElapsedTimeSinceLastMessage() > PING_TIMEOUT / 2) {
                        clientService.get().requestService(new Call(null, Service.Ping, null));
                        setLastMessage();
                    }
                    if(clientService.get().hasEvent()){
                        ServerEvent event = clientService.get().pollEvent();
                        while(event != null){
                            synchronized (eventQueue){
                                eventQueue.add(event);
                            }
                            event = clientService.get().pollEvent();
                        }
                        synchronized (NetworkManager.instance){
                            NetworkManager.instance.notifyAll();
                        }
                    }
                }
            }catch(Exception e){
                disconnect();
            }
        }
    }

    /**
     * Method that checks if the object is connected, then sets the `connected` flag to false.
     * It then adds a `ServerDisconnect` event to the `eventQueue` and notifies all threads waiting on the `instance` object.
     * The `synchronized` keyword ensures that only one thread can execute this method at a time.
     *
     * @author Lorenzo
     */
    @Override
    synchronized public void disconnect() {
        if(isConnected()){
            setConnected(false);
            synchronized (eventQueue){
                eventQueue.add(ServerEvent.ServerDisconnect());
            }
            synchronized (instance) {
                instance.notifyAll();
            }
        }
    }

    /**
     * Method that attempts to reconnect to a server using the `serverInfo` object,
     * and if it is not null, it calls the `connect()` method with the `serverInfo` parameter.
     * If the `serverInfo` object is null, it throws a `ClientNeverConnectedException`.
     *
     * @author Lorenzo
     */
    @Override
    public void reconnect() throws Exception {
        if(serverInfo != null) {
            connect(serverInfo);
        }
        throw new ClientNeverConnectedException();
    }

    /**
     * Method that return a boolean value indicating whether the object is currently connected or not.
     * @return connected.
     *
     * @author Lorenzo
     */
    @Override
    public boolean isConnected() {
        synchronized (connectedLock){
            return connected;
        }
    }

    /**
     * Method that returns an `Optional` object containing a `ServerEvent` object.
     * The method is synchronized to ensure thread safety.
     * It uses the `poll()` method of the `eventQueue` object to retrieve and remove the first element of the queue,
     * and returns it wrapped in an `Optional` object.
     * If the queue is empty, it returns an empty `Optional`.
     * @return an `Optional` object containing a `ServerEvent` object.
     *
     * @author Lorenzo
     */
    @Override
    public Optional<ServerEvent> getEvent() {
        synchronized (eventQueue){
            return Optional.ofNullable(eventQueue.poll());
        }
    }

    /**
     * Method that returns a boolean value indicating whether the `eventQueue` is empty or not.
     * @return true if the `eventQueue` is empty, false otherwise.
     *
     * @author Lorenzo
     */
    @Override
    public boolean hasEvent() {
        synchronized (eventQueue){
            return !eventQueue.isEmpty();
        }
    }

    /**
     * Method that returns a function that takes a boolean value and returns an ArrayList of Lobby objects.
     * It throws an exception if an error occurs.
     * The function is created by calling the handleService method
     * with a new Function object that takes a boolean value of TRUE and a Service value of LobbyList.
     * @return a function that takes a boolean value and returns an ArrayList of Lobby objects.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, ArrayList<Lobby>> lobbyList() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyList));
    }

    /**
     * Method that takes a `LobbyCreateInfo` object as input and returns a `Lobby` object.
     * It also throws an exception if an error occurs.
     * The method calls the `handleService` method
     * with a `Function` object that takes the `LobbyCreateInfo` object and a `Service` enum value as input.
     * The `handleService` method is responsible for processing the request and returning the appropriate response.
     * @param info the name of the lobby to create
     * @return a function that takes a LobbyCreateInfo object and returns a Lobby object.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo info) throws Exception {
        return handleService(new Function(info, Service.LobbyCreate));
    }

    /**
     * Method that takes a `String` parameter called `lobbyName` and returns a `Function` that maps a `String` to a `Lobby` object.
     * The method throws an `Exception` if an error occurs.
     * The `handleService` method is called with a new `Function` object that takes the `lobbyName` parameter and a `Service` enum value called `LobbyJoin`.
     * The purpose of this method is to handle a service request to join a lobby with the specified name.
     * @param lobbyName the name of the lobby to join
     * @return a function that takes a String parameter and returns a Lobby object.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception {
        return handleService(new Function(lobbyName, Service.LobbyJoin));
    }

    /**
     * Method that returns a function that takes a boolean value and returns a boolean value.
     * The method throws an exception if an error occurs.
     * The `handleService` method is called with a new `Function` object that takes a boolean value of TRUE and a `Service` enum value of `LobbyLeave`.
     * The purpose of this method is to handle a service request to leave a lobby.
     * @return a function that takes a boolean value and returns a boolean value.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, Boolean> lobbyLeave() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyLeave));
    }

    /**
     * Method called `updateLobby()` that returns a `Function` object that takes a `Boolean` parameter and returns a `Lobby` object.
     * The method also throws an `Exception`.
     * The `handleService()` method is called with a new `Function` object as its argument, which is created with a `Boolean.TRUE` value and a `Service.LobbyUpdate` value.
     * The purpose of this method is to handle a service request to update a lobby.
     * @return a function that takes a boolean value and returns a Lobby object.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, Lobby> updateLobby() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyUpdate));
    }

    /**
     * Method that returns a `Function` object that takes a `Boolean` parameter and returns a `Boolean` value.
     * The method also throws an `Exception`.
     * The `handleService()` method is called with a new `Function` object as its argument, which is initialized with a `Boolean.TRUE` value and a `Service.GameStart` enum value.
     * The purpose of this method is to handle a service request to start a game.
     * @return a function that takes a boolean value and returns a boolean value.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Boolean, Boolean> gameStart() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.GameStart));
    }

    /**
     * Method that returns a `Function` that takes a `Boolean` as input and returns a `Boolean` as output.
     * It throws an `Exception` if there is an error while handling the service.
     * The `handleService` method is called with a new `Function` object that has a `Boolean.TRUE` value and a `Service.GameLoad` enum value as parameters.
     * The purpose of this method is to handle a service request to load a game.
     * @return a function that takes a boolean value and returns a boolean value.
     * @throws Exception
     *
     * @author Marco
     */
    @Override
    public Function<Boolean, Boolean> gameLoad() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.GameLoad));
    }

    /**
     * Method that takes a parameter of type `CardSelect` and returns a `Function` that takes a `CardSelect` and returns a `Boolean`.
     * It also throws an `Exception`.
     * The method calls the `handleService` method with a new `Function` object created with the `selected` parameter and the `Service.CardSelect` enum value.
     * The purpose of this method is to handle a service request to select a card.
     * @param selected the cards selected
     * @return a function that takes a CardSelect object and returns a boolean value.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<CardSelect, Boolean> cardSelect(CardSelect selected) throws Exception {
        return handleService(new Function(selected, Service.CardSelect));
    }

    /**
     * Method that returns a `Function<Boolean, Boolean>` object.
     * The method calls the `handleService()` method with a new `Function` object that takes a `Boolean` parameter and returns a `Boolean` value.
     * The `Function` object is created with the parameters `true` and `Service.ExitGame`.
     * The `handleService()` method is responsible for handling the `Service` object passed to it and returning a `Boolean` value.
     * The purpose of this method is to handle a service request to exit the game.
     *
     * @return a function that takes a boolean value and returns a boolean value.
     * @throws Exception
     *
     * @author Marco
     */
    @Override
    public Function<Boolean, Boolean> exitGame() throws Exception {
        return handleService(new Function(true, Service.ExitGame));
    }

    /**
     * Method that takes a `Login` object as input and returns a `Function` object that takes a `Login` object as input and returns a `Serializable` object.
     * The method also throws an `Exception`.
     * The purpose of this method is to handle a service request to log in.
     * @param info the username to log in with
     * @return a function that takes a Login object and returns a Serializable object.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Login, Serializable> login(Login info) throws Exception {
        Function fn = new Function(info, Service.Login);
        Result<Serializable> result;
        try{
            result = loginService.login(info, fn.id());
            if(result.isOk()){
                clientService = Optional.of((ClientService) registry.lookup(info.username()));
            }
        }catch (RemoteException re){
            disconnect();
            throw re;
        }catch(Exception e){
            result = Result.err(e,fn.id());
        }
        fn.setResult(result);
        return fn;
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
     * Method that takes a `Message` object as input and returns a `Function` object that takes a `Message` object as input and returns a `Boolean` value.
     * The method calls the `handleService` method with a new `Function` object that takes the input `Message` object and a `Service` enum value `GameChatSend`.
     * The purpose of this method is to handle a service request to send a message.
     * @param message the message to send
     * @return a function that takes a Message object and returns a boolean value.
     * @throws Exception
     *
     * @author Lorenzo
     */
    @Override
    public Function<Message, Boolean> chat(Message message) throws Exception {
        return handleService(new Function(message, Service.GameChatSend));
    }

    /**
     * Wrapper to handle the service call.
     * @param fn the function to handle.
     * @return the function with the result.
     * @throws Exception if an error occurs while communicating with the server.
     *
     * @autor Lorenzo
     */
    private Function handleService(Function fn) throws Exception{
        Result<Serializable> result;
        try{
            if(clientService.isEmpty()){
                throw new ClientNotIdentifiedException();
            }
            result = clientService.get().requestService(fn.getCall());
        }catch (RemoteException re){
            disconnect();
            throw re;
        }catch(Exception e){
            result = Result.err(e,fn.id());
        }
        fn.setResult(result);
        setLastMessage();
        return fn;
    }

}
