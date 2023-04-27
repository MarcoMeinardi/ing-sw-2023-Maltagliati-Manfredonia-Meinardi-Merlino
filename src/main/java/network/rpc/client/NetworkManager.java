package network.rpc.client;

import controller.lobby.Lobby;
import network.rpc.Result;
import network.rpc.ServerEvent;
import network.rpc.Service;
import network.rpc.parameters.CardSelect;
import network.rpc.parameters.NewLobby;

import java.io.Serializable;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.logging.Logger;

public class NetworkManager extends Thread{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Boolean connected = false;
    private Server server;
    private static NetworkManager instance;
    private HashMap<UUID,Function> callQueue = new HashMap<UUID,Function>();
    private Queue<ServerEvent> eventQueue = new LinkedList<ServerEvent>();
    private Logger logger = Logger.getLogger(NetworkManager.class.getName());
    private Function<LocalDateTime,Boolean> lastPing = null;
    private static final int PING_TIMEOUT = 60;
    private NetworkManager(){}
    public static NetworkManager getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }
    public void connect(Server server) throws Exception{
        setConnected(false);
        this.server = server;
        this.socket = new Socket(server.ip(), server.port());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        testConnection();
        setConnected(true);
        this.start();
        logger.info("Connected to server");
    }
    public void disconnect(){
        logger.info("Disconnecting from server");
        setConnected(false);
        try{
            this.socket.close();
        }catch(Exception e){
            logger.warning(e.getMessage());
        }
    }

    private Optional<Result<Serializable>> receive() throws Exception{
        synchronized(in){
            if(in.available() <= 0){
                return Optional.empty();
            }
            Object obj = in.readObject();
            if(!(obj instanceof Result)){
                throw new Exception("Invalid object received");
            }
            return Optional.of((Result<Serializable>)obj);
        }
    }

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
    private long secondsSinceLastPing(){
        if(lastPing == null){
            return 0;
        }
        return Duration.between(lastPing.getParams(), LocalDateTime.now()).getSeconds();
    }
    public void run(){
        while(isConnected()){
            try{
                Optional<Result<Serializable>> result = receive();//read all incoming messages
                while(result.isPresent()){
                    try{
                        if(result.get().isEvent()){
                            ServerEvent event = (ServerEvent)result.get().unwrap();
                            synchronized (eventQueue){
                                eventQueue.add(event);
                            }
                        }else{
                            Function caller;
                            synchronized (callQueue){
                                caller = callQueue.get(result.get().id());
                            }
                            caller.setResult(result.get());
                        }
                    }catch(Exception e){
                        logger.warning(e.getMessage());
                    }
                    result = receive();
                }
                long elapsedTime = this.secondsSinceLastPing();//check if we need to ping
                if(elapsedTime > this.PING_TIMEOUT/2){
                    if(lastPing.checkResult().isPresent()){//if we have a ping result ping again
                        ping();
                    }else{
                        if(elapsedTime > this.PING_TIMEOUT){//if we don't have a ping result and we have waited too long, disconnect
                            disconnect();
                        }
                    }
                }
            }catch(Exception e){
                logger.warning(e.getMessage());
                disconnect();
            }
        }
    }
    public void reconnect() throws Exception{
        disconnect();
        this.join();
        connect(server);
    }

    private void setConnected(boolean connected){
        synchronized (this.connected){
            this.connected = connected;
        }
    }
    public boolean isConnected(){
        synchronized (connected){
            return connected;
        }
    }
    public Optional<ServerEvent> getEvent(){
        synchronized (eventQueue){
            if(eventQueue.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(eventQueue.remove());
        }
    }

    public Function<Boolean,ArrayList<Lobby>> lobbyList() throws Exception{
        Function<Boolean,ArrayList<Lobby>> lobbyList = new Function<Boolean,ArrayList<Lobby>>(true, Service.LobbyList);
        lobbyList.call(out);
        return lobbyList;
    }
    public Function<NewLobby,Boolean> lobbyCreate(NewLobby lobby) throws Exception{
        Function<NewLobby,Boolean> lobbyCreate = new Function<NewLobby,Boolean>(lobby, Service.LobbyCreate);
        lobbyCreate.call(out);
        return lobbyCreate;
    }
    public Function<String,Boolean> lobbyJoin(String lobbyName) throws Exception{
        Function<String,Boolean> lobbyJoin = new Function<String,Boolean>(lobbyName, Service.LobbyJoin);
        lobbyJoin.call(out);
        return lobbyJoin;
    }
    public Function<Boolean,Boolean> lobbyLeave() throws Exception{
        Function<Boolean,Boolean> lobbyLeave = new Function<Boolean,Boolean>(true, Service.LobbyLeave);
        lobbyLeave.call(out);
        return lobbyLeave;
    }
    public Function<Boolean,Boolean> gameStart() throws Exception{
        Function<Boolean,Boolean> gameStart = new Function<Boolean,Boolean>(true, Service.GameStart);
        gameStart.call(out);
        return gameStart;
    }
    public Function<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception{
        Function<CardSelect,Boolean> cardSelect = new Function<CardSelect,Boolean>(selected, Service.CardSelect);
        cardSelect.call(out);
        return cardSelect;
    }
    private Function<LocalDateTime,Boolean> ping() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        Function<LocalDateTime,Boolean> ping = new Function<LocalDateTime,Boolean>(now, Service.Ping);
        this.lastPing = ping;
        ping.call(out);
        return ping;
    }
    public Function<String,Boolean> login(String username) throws Exception{
        Function<String,Boolean> login = new Function<String,Boolean>(username, Service.Login);
        login.call(out);
        return login;
    }
}
