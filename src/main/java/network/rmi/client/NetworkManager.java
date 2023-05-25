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
import network.rmi.server.Client;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

public class NetworkManager extends Thread implements NetworkManagerInterface {
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
    @Override
    public void connect(Server server) throws Exception {
        registry = LocateRegistry.getRegistry(server.ip(), server.port());
        clientService = Optional.empty();
        loginService = (LoginService) registry.lookup("LoginService");
        setConnected(true);
        pingTime = LocalDateTime.now();
        serverInfo = server;
        this.start();
    }

    private void setConnected(boolean connected){
        synchronized (connectedLock){
            this.connected = connected;
        }
    }

    public static NetworkManagerInterface getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }

    private void setLastMessage(){
        synchronized (lastMessageLock){
            this.pingTime = LocalDateTime.now();
        }
    }

    private long getElapsedTimeSinceLastMessage(){
        synchronized (lastMessageLock){
            return Duration.between(pingTime, LocalDateTime.now()).getSeconds();
        }
    }

    @Override
    public void run(){
        logger.info("NetworkManager: running");
        while(isConnected()){
            try{
                if(clientService.isPresent() && getElapsedTimeSinceLastMessage() > PING_TIMEOUT/2){
                    clientService.get().requestService(new Call(null, Service.Ping, null));
                    setLastMessage();
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
        try{
            if(clientService.isPresent()){
                    return Optional.of(clientService.get().pollEvent());
            }
        }catch (Exception e){
            disconnect();
        }
        return event;
    }

    @Override
    public boolean hasEvent() {
        try{
            if(clientService.isPresent()){
                return clientService.get().hasEvent();
            }
        }catch (Exception e){
            disconnect();
        }
        return false;
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

    @Override
    public Function<String, Boolean> chat(String message) throws Exception {
        return handleService(new Function(message, Service.GameChatSend));
    }

    public Function handleService(Function fn){
        if(hasEvent()){
            logger.info("Client has events");
        }else{
            logger.info("Client has no events");
        }
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
        setLastMessage();
        return fn;
    }

}
