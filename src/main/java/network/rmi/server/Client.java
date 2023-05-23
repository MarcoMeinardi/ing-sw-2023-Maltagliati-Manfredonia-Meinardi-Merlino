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

public class Client implements ClientService, ClientInterface {
    ClientStatusHandler statusHandler;
    Queue<ServerEvent> serverEvents = new LinkedList<>();
    BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> callHandler;
    Object handlerLock = new Object();
    String username;
    LocalDateTime lastMessageTime = LocalDateTime.now();
    Object messageTimeLock = new Object();
    ClientService stub;
    private static final DateTimeFormatter format = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private Object lastMessageTimeLock = new Object();

    public static final int TIMEOUT = 60;

    public Client(String username , Registry registry, int port) throws RemoteException {
        this.username = username;
        statusHandler = new ClientStatusHandler();
        stub = (ClientService) UnicastRemoteObject.exportObject(this, port);
        setCallHandler(LobbyController.getInstance()::handleLobbySearch);
        registry.rebind(username, stub);
    }
    @Override
    public ClientStatus getStatus() {
        return statusHandler.getStatus();
    }

    @Override
    public void setStatus(ClientStatus status) {
        logger.info("Setting status of " + username + " to " + status.toString());
        statusHandler.setStatus(status);
    }
    @Override
    public <T extends Serializable> void sendEvent(ServerEvent<T> message){
        serverEvents.add(message);
    }
    @Override
    public boolean isDisconnected() {
        return statusHandler.getStatus() == ClientStatus.Disconnected;
    }

    @Override
    public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler) {
        synchronized (handlerLock){
            callHandler = handler;
        }
    }

    @Override
    public String getUsername() throws ClientNotIdentifiedException {
        if(username == null){
            throw new ClientNotIdentifiedException();
        }
        return username;
    }

    @Override
    public LocalDateTime getLastMessageTime() {
        synchronized (messageTimeLock){
            return lastMessageTime;
        }
    }

    @Override
    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler() {
        synchronized (handlerLock){
            return callHandler;
        }
    }

    @Override
    public Result requestService(Call call) {
        logger.info("Received call " + call.toString() + " from " + username);
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
     * @return the next event for the event queue of the client, or null if there are no events
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

    @Override
    public Boolean hasEvent() throws RemoteException {
        synchronized (serverEvents){
            return !serverEvents.isEmpty();
        }
    }

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
}
