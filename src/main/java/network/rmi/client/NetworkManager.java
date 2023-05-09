package network.rmi.client;

import controller.lobby.Lobby;
import network.*;
import network.errors.ClientAlreadyConnectedExeption;
import network.errors.ClientNeverConnectedException;
import network.errors.ClientNotIdentifiedException;
import network.parameters.CardSelect;
import network.parameters.LobbyCreateInfo;
import network.parameters.Login;
import network.rmi.ClientService;
import network.rmi.LoginService;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

public class NetworkManager extends Thread implements NetworkManagerInterface {
    private static NetworkManager instance = null;
    private Registry registry;
    private Optional<ClientService> clientService;
    private LoginService loginService;
    private LinkedList<ServerEvent> eventQueue;

    private boolean connected;
    private final Object connectedLock = new Object();

    private LocalDateTime lastMessage;
    private final Object lastMessageLock = new Object();
    private final Function<Boolean,Boolean> ping = new Function(Boolean.TRUE,Service.Ping);
    private final int PING_TIMEOUT = 60;

    private Server serverInfo;
    @Override
    public void connect(Server server) throws Exception {
        registry = LocateRegistry.getRegistry(server.ip(), server.port());
        clientService = Optional.empty();
        loginService = (LoginService) registry.lookup("LoginService");
        eventQueue = new LinkedList<>();
        setConnected(true);
        lastMessage = LocalDateTime.now();
        serverInfo = server;
        this.start();
    }

    private void setConnected(boolean connected){
        synchronized (connectedLock){
            this.connected = connected;
        }
    }

    private void setLastMessage(){
        synchronized (lastMessageLock){
            this.lastMessage = LocalDateTime.now();
        }
    }

    private long getElapsedTimeSinceLastMessage(){
        synchronized (lastMessageLock){
            return Duration.between(lastMessage, LocalDateTime.now()).getSeconds();
        }
    }

    @Override
    public void run(){
        while(isConnected()){
            try{
                if(getElapsedTimeSinceLastMessage() > PING_TIMEOUT/2){
                    if(ping.checkResult().isPresent()){
                        if(clientService.isPresent()){
                            ping.setResult(clientService.get().requestService(ping.getCall()));
                        }else{
                            if(getElapsedTimeSinceLastMessage() > PING_TIMEOUT){
                                disconnect();
                            }
                        }
                    }
                }
                if(clientService.isPresent()){
                    ServerEvent e = clientService.get().pollEvent();
                    while(e != null){
                        synchronized (eventQueue){
                            eventQueue.add(e);
                        }
                        e = clientService.get().pollEvent();
                    }
                }
            }catch(Exception e){
                disconnect();
            }
        }
    }

    @Override
    public void disconnect() {
        setConnected(false);
    }

    @Override
    public void reconnect() throws Exception {
        if(serverInfo != null) {
            connect(serverInfo);
        }
        throw new ClientNeverConnectedException();
    }

    @Override
    public boolean isConnected() {
        synchronized (connectedLock){
            return connected;
        }
    }

    @Override
    public Optional<ServerEvent> getEvent() {
        Optional<ServerEvent> event = Optional.empty();
        synchronized (eventQueue){
            if(!eventQueue.isEmpty()){
                event = Optional.of(eventQueue.poll());
            }
        }
        return event;
    }

    @Override
    public boolean hasEvent() {
        synchronized (eventQueue){
            return !eventQueue.isEmpty();
        }
    }

    @Override
    public Function<Boolean, ArrayList<Lobby>> lobbyList() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyList));
    }

    @Override
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo info) throws Exception {
        return handleService(new Function(info, Service.LobbyCreate));
    }

    @Override
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception {
        return handleService(new Function(lobbyName, Service.LobbyJoin));
    }

    @Override
    public Function<Boolean, Boolean> lobbyLeave() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyLeave));
    }

    @Override
    public Function<Boolean, Lobby> updateLobby() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.LobbyUpdate));
    }

    @Override
    public Function<Boolean, Boolean> gameStart() throws Exception {
        return handleService(new Function(Boolean.TRUE, Service.GameStart));
    }

    @Override
    public Function<CardSelect, Boolean> cardSelect(CardSelect selected) throws Exception {
        return handleService(new Function(selected, Service.CardSelect));
    }

    @Override
    public Function<Login, Boolean> login(Login info) throws Exception {
        Function fn = new Function(info, Service.Login);
        Result<Serializable> result;
        try{
            if(!loginService.login(info)){
                throw new ClientAlreadyConnectedExeption();
            }
            clientService = Optional.of((ClientService) registry.lookup(info.username()));
            result = Result.empty(fn.id());
        }catch(Exception e){
            result = Result.err(e,fn.id());
        }
        fn.setResult(result);
        return fn;
    }

    public Function handleService(Function fn){
        Result<Serializable> result;
        try{
            if(clientService.isEmpty()){
                throw new ClientNotIdentifiedException();
            }
            result = clientService.get().requestService(fn.getCall());
        }catch(Exception e){
            result = Result.err(e,fn.id());
        }
        fn.setResult(result);
        return fn;
    }

}
