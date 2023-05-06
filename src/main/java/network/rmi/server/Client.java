package network.rmi.server;

import network.*;
import network.errors.ClientNotIdentifiedException;
import network.errors.DisconnectedClientException;
import network.rmi.ClientService;

import java.io.Serializable;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiFunction;

public class Client implements ClientService, ClientInterface {
    ClientStatusHandler statusHandler;
    Queue<ServerEvent> serverEvents = new LinkedList<>();
    BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> callHandler;
    Object handlerLock = new Object();
    String username;
    LocalDateTime lastMessageTime = LocalDateTime.now();
    Object messageTimeLock = new Object();
    ClientService stub;
    public Client(String username , Registry registry, int port) throws Exception {
        this.username = username;
        stub = (ClientService) UnicastRemoteObject.exportObject(this, port);
        registry.rebind(username, stub);
    }
    @Override
    public ClientStatus getStatus() {
        return statusHandler.getStatus();
    }

    @Override
    public void setStatus(ClientStatus status) {
        statusHandler.setStatus(status);
    }

    @Override
    public ClientStatus getLastValidStatus() {
        return statusHandler.getLastValidStatus();
    }

    @Override
    public <T extends Serializable> void send(ServerEvent<T> message) throws Exception {
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
}
