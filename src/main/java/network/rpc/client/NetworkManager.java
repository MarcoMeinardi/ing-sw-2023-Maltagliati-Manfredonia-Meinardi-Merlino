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

public class NetworkManager extends Thread implements NetworkManagerInterface {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Boolean connected = false;
    private Object connectedLock = new Object();
    private Server server;
    private static NetworkManager instance;
    private HashMap<UUID, Function> callQueue = new HashMap<UUID,Function>();
    private Queue<ServerEvent> eventQueue = new LinkedList<ServerEvent>();
    private Logger logger = Logger.getLogger(NetworkManager.class.getName());
    private Function<LocalDateTime,Boolean> lastPing = null;
    private static final int PING_TIMEOUT = 1;
    private Thread checkPingThread;
    private NetworkManager(){}

    public static NetworkManager getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }

    @Override
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

    @Override
    public void disconnect(){
        logger.info("Disconnecting from server");
        setConnected(false);
        try{
            this.socket.close();
        }catch(Exception e) {
            logger.warning(e.getMessage());
        }
        try{
            this.join();
        }catch(Exception e) {
            logger.warning(e.getMessage());
        }
    }

    private Result<Serializable> receive() throws Exception{
        synchronized(in){
            Object obj = in.readObject();
            if(!(obj instanceof Result)){
                throw new Exception("Invalid object received");
            }
            return (Result<Serializable>)obj;
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

    public void run() {
        checkPingThread = new Thread(this::checkPing);
        checkPingThread.start();
        while(isConnected()){
            try {
                Result result = receive();
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

    @Override
    public void reconnect() throws Exception{
        disconnect();
        connect(server);
    }

    private void setConnected(boolean connected){
        synchronized (connectedLock){
            this.connected = connected;
        }
    }

    @Override
    public boolean isConnected(){
        synchronized (connectedLock){
            return connected;
        }
    }

    @Override
    public Optional<ServerEvent> getEvent(){
        synchronized (eventQueue){
            if(eventQueue.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(eventQueue.remove());
        }
    }

    @Override
    public boolean hasEvent() {
        synchronized (eventQueue){
            return !eventQueue.isEmpty();
        }
    }

    @Override
    public Function<Boolean,ArrayList<Lobby>> lobbyList() throws Exception{
        Function<Boolean,ArrayList<Lobby>> lobbyList = new Function<Boolean,ArrayList<Lobby>>(true, Service.LobbyList);
        callQueue.put(lobbyList.id(), lobbyList);
        lobbyList.call(out);
        return lobbyList;
    }

    @Override
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo info) throws Exception {
        Function<LobbyCreateInfo, Lobby> lobbyCreate = new Function<LobbyCreateInfo, Lobby>(info, Service.LobbyCreate);
        callQueue.put(lobbyCreate.id(), lobbyCreate);
        lobbyCreate.call(out);
        return lobbyCreate;
    }

    @Override
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception {
        Function<String, Lobby> lobbyJoin = new Function<String, Lobby>(lobbyName, Service.LobbyJoin);
        callQueue.put(lobbyJoin.id(), lobbyJoin);
        lobbyJoin.call(out);
        return lobbyJoin;
    }

    @Override
    public Function<Boolean, Boolean> lobbyLeave() throws Exception {
        Function<Boolean, Boolean> lobbyLeave = new Function<Boolean, Boolean>(true, Service.LobbyLeave);
        callQueue.put(lobbyLeave.id(), lobbyLeave);
        lobbyLeave.call(out);
        return lobbyLeave;
    }

    @Override
    public Function<Boolean, Lobby> updateLobby() throws Exception {
        Function<Boolean, Lobby> lobbyUpdate = new Function<Boolean, Lobby>(true, Service.LobbyUpdate);
        callQueue.put(lobbyUpdate.id(), lobbyUpdate);
        lobbyUpdate.call(out);
        return lobbyUpdate;
    }

    @Override
    public Function<Boolean,Boolean> gameStart() throws Exception{
        Function<Boolean,Boolean> gameStart = new Function<Boolean,Boolean>(true, Service.GameStart);
        callQueue.put(gameStart.id(), gameStart);
        gameStart.call(out);
        return gameStart;
    }

    @Override
    public Function<Boolean, Boolean> gameLoad() throws Exception {
        Function<Boolean,Boolean> gameLoad = new Function<Boolean,Boolean>(true, Service.GameLoad);
        callQueue.put(gameLoad.id(), gameLoad);
        gameLoad.call(out);
        return gameLoad;
    }

    @Override
    public Function<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception{
        Function<CardSelect,Boolean> cardSelect = new Function<CardSelect,Boolean>(selected, Service.CardSelect);
        callQueue.put(cardSelect.id(), cardSelect);
        cardSelect.call(out);
        return cardSelect;
    }

    @Override
    public Function<Login, Serializable> login(Login username) throws Exception {
        Function login = new Function(username, Service.Login);
        callQueue.put(login.id(), login);
        login.call(out);
        return login;
    }

    @Override
    public Function<Message, Boolean> chat(Message message) throws Exception {
        Function<Message, Boolean> chat = new Function<Message, Boolean>(message, Service.GameChatSend);
        callQueue.put(chat.id(), chat);
        chat.call(out);
        return chat;
    }
}
