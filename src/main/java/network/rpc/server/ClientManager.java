package network.rpc.server;

import controller.lobby.LobbyController;
import network.*;
import network.errors.ClientAlreadyConnectedExeption;
import network.errors.ClientNotIdentifiedException;
import network.parameters.Login;
import network.parameters.WrongParametersException;

import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

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

    public static ClientManagerInterface getInstance() throws Exception {
        synchronized (instanceLock){
            if(instance == null){
                instance = new ClientManager(port);
                instance.start();
            }
            return instance;
        }
    }

    private ClientManager(int port) throws Exception{
        this.socket = new ServerSocket(port);
        this.acceptConnectionsThread = new Thread(this::acceptConnections);
    }

    private Result<Serializable> registerService(Call<Serializable> call, ClientInterface client){
        if(call.service() != Service.Login){
            return Result.err(new ClientNotIdentifiedException(), call.id());
        }
        if(!(call.params() instanceof Login)){
            return Result.err(new WrongParametersException("Login",call.params().getClass().getName(),"call.param()"), call.id());
        }
        Login login = (Login) call.params();
        try{
            addIdentifiedClient(login.username(), client);
            return Result.empty(call.id());//change handler to lobby handler
        }catch (Exception e){
            return Result.err(e, call.id());
        }
    }
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
    private void addUnidentifiedClient(Client client){
        synchronized (unidentifiedClients) {
            unidentifiedClients.add(client);
        }
    }

    private void addIdentifiedClient(String username, ClientInterface client) throws ClientAlreadyConnectedExeption {
        synchronized (identifiedClients) {
            if(identifiedClients.containsKey(username) && identifiedClients.get(username).getStatus() != ClientStatus.Disconnected){
                throw new ClientAlreadyConnectedExeption();
            }
            if(identifiedClients.containsKey(username)){
                identifiedClients.get(username).disconnect();
                ClientInterface lastClient = identifiedClients.get(username);
                if (lastClient.getLastValidStatus().equals(ClientStatus.Disconnected)) {
                    client.setStatus(ClientStatus.Idle);
                } else {
                    client.setStatus(lastClient.getLastValidStatus());
                }
                client.setCallHandler(lastClient.getCallHandler());
            }else{
                client.setCallHandler(LobbyController.getInstance()::handleLobbySearch);
            }
            ((Client)client).setUsername(username);
            identifiedClients.put(username, (Client)client);
            unidentifiedClients.remove(client);
        }
    }

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
                Thread.sleep(Client.TIMEOUT / 2);
                synchronized (instanceLock){
                    running = instance != null;
                }
            }
        }catch(InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            acceptConnectionsThread.interrupt();
        }
    }

    public Optional<ClientInterface> getClient(String username){
        Optional<ClientInterface> client = Optional.empty();
        synchronized (identifiedClients) {
            if(identifiedClients.containsKey(username)){
                client = Optional.of(identifiedClients.get(username));
            }
        }
        return client;
    }

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

    public boolean isClientConnected(String username){
        synchronized (identifiedClients) {
            return identifiedClients.containsKey(username) && identifiedClients.get(username).getStatus() != ClientStatus.Disconnected;
        }
    }
}
