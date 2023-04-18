package network.rpc.server;

import network.rpc.Call;
import network.rpc.Result;
import network.rpc.Service;
import network.rpc.parameters.WrongParametersException;

import java.io.Serializable;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

public class ClientManager extends Thread{
    final private LinkedList<Client> unidentified_clients = new LinkedList<>();
    final private HashMap<String, Client> identified_clients = new HashMap<>();

    private ServerSocket socket;

    private Thread acceptConnectionsThread;
    private Thread identifyClientsThread;

    public ClientManager(int port) throws Exception{
        this.socket = new ServerSocket(port);
        this.acceptConnectionsThread = new Thread(this::acceptConnections);
        this.identifyClientsThread = new Thread(this::identifyClients);
    }

    private void acceptConnections(){
        while(true){
            try{
                Client client = new Client(socket.accept());
                addUidentifiedClient(client);
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
    public void identifyClients(){
        while(true){
            synchronized (unidentified_clients) {
                for (Client client : unidentified_clients) {
                    Optional<Call<Serializable>> msg;
                    try{
                        msg = client.receive();
                        if(msg.isEmpty()){
                            continue;
                        }
                        if(msg.get().getService() == Service.Login){
                            Result<Boolean> result;
                            if(msg.get().getParams() instanceof network.rpc.parameters.Login){
                                String username = ((network.rpc.parameters.Login)msg.get().getParams()).username();
                                addIdentifiedClient(username, client);
                                unidentified_clients.remove(client);
                                result = Result.ok(true, msg.get().getId());
                            }else{
                                String class_type = msg.get().getParams().getClass().toString();
                                result = Result.err(new WrongParametersException("String", class_type, "Login"), msg.get().getId());
                            }
                            client.send(result);
                        }
                    }catch (Exception e) {
                        Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                        client.disconnect();
                        unidentified_clients.remove(client);
                    }
                }
            }
        }
    }

    public void run(){
        acceptConnectionsThread.start();
        identifyClientsThread.start();
        try{
            acceptConnectionsThread.join();
            identifyClientsThread.join();
        }catch(InterruptedException e){
            Logger.getLogger(Client.class.getName()).warning(e.getMessage());
            acceptConnectionsThread.interrupt();
            identifyClientsThread.interrupt();
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

    protected HashMap<String, Client> getClients(){
        synchronized (identified_clients) {
            return identified_clients;
        }
    }
}
