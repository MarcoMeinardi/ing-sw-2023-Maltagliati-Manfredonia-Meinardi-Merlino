package network.rmi.server;

import controller.lobby.LobbyController;
import network.*;
import network.errors.ClientNotIdentifiedException;
import network.errors.DisconnectedClientException;
import network.rmi.ClientService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * Class to represent a client connected to the server via rmi.
 */
public class Client implements ClientService, ClientInterface {
    ClientStatusHandler statusHandler;
    private final Queue<ServerEvent> serverEvents = new LinkedList<>();
    BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> callHandler;
    final Object handlerLock = new Object();
    String username;
    LocalDateTime lastMessageTime = LocalDateTime.now();
    final Object messageTimeLock = new Object();
    ClientService stub;
    private static final DateTimeFormatter format = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private Object lastMessageTimeLock = new Object();

    public static final int TIMEOUT = 5;

    /**
     * Constructor of the class.
     * @param username Username of the client.
     * @param registry Registry of the server.
     * @param port Port of the client.
     * @throws RemoteException If an error occurs.
     */
    public Client(String username , Registry registry, int port) throws RemoteException {
        this.username = username;
        statusHandler = new ClientStatusHandler();
        stub = (ClientService) UnicastRemoteObject.exportObject(this, port);
        setCallHandler(LobbyController.getInstance()::handleLobbySearch);
        registry.rebind(username, stub);
    }

    /**
     * Getter for the client's status
     * @return the client status
     */
    @Override
    public ClientStatus getStatus() {
        return statusHandler.getStatus();
    }

    /**
     * Setter for the client's status
     * @param status the `ClientStatus` to be set to
     */
    @Override
    public void setStatus(ClientStatus status) {
        logger.info("Setting status of " + username + " to " + status.toString());
        statusHandler.setStatus(status);
    }

    /**
     * Setter for the client's last valid status
     * used only for disconnected clients
     * @param status the `ClientStatus` to be set to
     */
    @Override
    public void setLastValidStatus(ClientStatus status) {
        logger.info("Setting last valid status of " + username + " to " + status.toString());
        statusHandler.setLastValidStatus(status);
    }

    /**
     * Add an event to the queue of messages to be sent
     * @param message the message to send
     */
    @Override
    public <T extends Serializable> void sendEvent(ServerEvent<T> message){
        serverEvents.add(message);
    }

    /**
     * Check if a client is disconnected
     * @return if the client is disconnected
     */
    @Override
    public boolean isDisconnected() {
        return statusHandler.getStatus() == ClientStatus.Disconnected;
    }

    /**
     * Setter for the client's call handler
     * @param handler the function to be set as the new handler
     */
    @Override
    public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler) {
        synchronized (handlerLock){
            callHandler = handler;
        }
    }

    /**
     * Getter for the client's username
     * @return the client username
     * @throws ClientNotIdentifiedException if the client exists, but has not logged in yet
     */
    @Override
    public String getUsername() throws ClientNotIdentifiedException {
        if(username == null){
            throw new ClientNotIdentifiedException();
        }
        return username;
    }

    /**
     * Getter for the `lastMessageTime` field
     * @return the `lastMessageTime` field
     */
    @Override
    public LocalDateTime getLastMessageTime() {
        synchronized (messageTimeLock){
            return lastMessageTime;
        }
    }

    /**
     * Getter for the call handler
     * @return the `callHandler` field
     */
    @Override
    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler() {
        synchronized (handlerLock){
            return callHandler;
        }
    }

    /**
     * Call the client handler with the given parameters
     * @param call the handler funciton parameters
     * @return a `Result` object containing the result of the handler call
     */
    @Override
    public Result requestService(Call call) {
        if(statusHandler.getStatus() == ClientStatus.Disconnected){
            statusHandler.setStatus(statusHandler.getLastValidStatus());
        }
        synchronized (messageTimeLock){
            lastMessageTime = LocalDateTime.now();
        }
        synchronized (handlerLock){
            return callHandler.apply(call, this);
        }
    }

    /**
     * Ask if there are any events in the queue
     * @return true if there are any events in the `serverEvents` queue
     */
    @Override
    public ServerEvent pollEvent() {
        synchronized (messageTimeLock){
            lastMessageTime = LocalDateTime.now();
        }
        synchronized (serverEvents){
            return serverEvents.poll();
        }
    }

    /**
     * Check if the event queue is empty
     * @return true if the event queue is empty
     */
    @Override
    public Boolean hasEvent() throws RemoteException {
        synchronized (serverEvents){
            return !serverEvents.isEmpty();
        }
    }

    /**
     * Check if the client has been unreachable for more than `TIMEOUT` seconds
     * @return true if the client is definitely unreachable
     */
    @Override
    public boolean checkPing(){
        if(statusHandler.getStatus() == ClientStatus.Disconnected){
            return false;
        }
        synchronized (lastMessageTimeLock) {
            if(lastMessageTime.plusSeconds(TIMEOUT).isBefore(LocalDateTime.now())){
                return false;
            }
        }
        return true;
    }

    /**
     * Method to clear the event queue and avoid old event to be pulled.
     */
    protected void clearEventQueue(){
        synchronized (serverEvents){
            serverEvents.clear();
        }
    }

    /**
     * Set the current client status to the last valid one
     */
    @Override
    public void recoverStatus(){
        statusHandler.setStatus(statusHandler.getLastValidStatus());
    }
}
