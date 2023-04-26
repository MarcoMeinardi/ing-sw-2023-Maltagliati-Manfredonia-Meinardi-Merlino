package network.rpc.server;

import controller.lobby.LobbyController;
import network.rpc.Call;
import network.rpc.Result;
import network.rpc.ServerEvent;
import network.rpc.Service;
import network.rpc.parameters.Login;
import network.rpc.parameters.WrongParametersException;

import java.io.Serializable;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

public class ClientManager extends Thread{
    final private LinkedList<Client> unidentified_clients = new LinkedList<>();
    final private HashMap<String, Client> identified_clients = new HashMap<>();
    private static final int TIMEOUT = 60;
    private ServerSocket socket;
    private Thread acceptConnectionsThread;


    public ClientManager(int port) throws Exception{
        this.socket = new ServerSocket(port);
        this.acceptConnectionsThread = new Thread(this::acceptConnections);
    }

    protected Result<Serializable> registerService(Call<Serializable> call, Client client){
        if(call.service() != Service.Login){
            return Result.err(new ClientNotIdentifiedException(), call.id());
        }
        if(!(call.params() instanceof Login)){
            return Result.err(new WrongParametersException("Login",call.params().getClass().getName(),"call.param()"), call.id());
        }
        Login login = (Login) call.params();
        try{
            client.setUsername(login.username());
            addIdentifiedClient(client.getUsername(), client);
            client.setCallHandler(LobbyController.getInstance()::handleLobby);
            return Result.empty(call.id());//change handler to lobby handler
        }catch (Exception e){
            return Result.err(e, call.id());
        }
    }

    protected Result<Serializable> lobbyService(Call<Serializable> call, Client client){
        return null;
    }

    private void acceptConnections(){
        while(true){
            try{
                Client client = new Client(socket.accept(), this::registerService);
                addUidentifiedClient(client);
                client.start();
            }catch (Exception e){
                Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            }
        }
    }
    protected void addUidentifiedClient(Client client){
        synchronized (unidentified_clients) {
            unidentified_clients.add(client);
        }
    }

    private void addIdentifiedClient(String username, Client client) throws ClientAlreadyConnectedExeption {
        synchronized (identified_clients) {
            if(identified_clients.containsKey(username) && identified_clients.get(username).getStatus() != ClientStatus.Disconnected){
                throw new ClientAlreadyConnectedExeption();
            }
            identified_clients.put(username, client);
        }
    }

    public void run(){
        acceptConnectionsThread.start();
        try{
            acceptConnectionsThread.join();
            while(true){
                synchronized (identified_clients) {
                    for (Client client : identified_clients.values()) {
                        if(client.getStatus() != ClientStatus.Disconnected){
                            if(Duration.between(client.getLastMessageTime(),LocalDateTime.now()).getSeconds() > TIMEOUT){
                                client.interrupt();
                                client.disconnect();
                            }
                        }
                    }
                }
                Thread.sleep(TIMEOUT/2);
            }
        }catch(InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            acceptConnectionsThread.interrupt();
        }
    }

    public Optional<Client> getClientByUsername(String username){
        Optional<Client> client = Optional.empty();
        synchronized (identified_clients) {
            if(identified_clients.containsKey(username)){
                client = Optional.of(identified_clients.get(username));
            }
        }
        return client;
    }

    public Optional<String> getUsernameByClient(Client client){
        Optional<String> username = Optional.empty();
        synchronized (identified_clients) {
            for(String key : identified_clients.keySet()){
                if(identified_clients.get(key) == client){
                    username = Optional.of(key);
                    break;
                }
            }
        }
        return username;
    }

    protected HashMap<String, Client> getClients(){
        synchronized (identified_clients) {
            return identified_clients;
        }
    }

    public boolean isClientConnected(String username){
        synchronized (identified_clients) {
            return identified_clients.containsKey(username) && identified_clients.get(username).getStatus() != ClientStatus.Disconnected;
        }
    }

    public boolean trySendToClient(String username, Result<Serializable> result){
        Optional<Client> client = getClientByUsername(username);
        if(client.isPresent()){
            try{
                client.get().send(result);
                return true;
            }catch (Exception e){
                Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                client.get().disconnect();
            }
        }
        return false;
    }

}
