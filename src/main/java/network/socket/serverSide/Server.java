package network.socket.serverSide;

import network.socket.protoGabibbo.LogIn;
import network.socket.protoGabibbo.Message;

import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

public class Server extends Thread{

    private ServerSocket socket;
    private LinkedList<Client> unidentified_clients = new LinkedList<Client>();
    private HashMap<String, Client> identified_clients = new HashMap<String, Client>();
    private Thread identification_thread;

    public Server(int port) throws Exception{
        socket = new ServerSocket(port);
        identification_thread = new Thread(this::waitingForIdentification);
    }

    private boolean addUidentifiedClient(Client client){
        synchronized (unidentified_clients) {
            return unidentified_clients.add(client);
        }
    }

    private void addIdentifiedClient(String username, Client client) throws ClientAlreadyConnectedExeption{
        synchronized (identified_clients) {
            if(identified_clients.containsKey(username) && identified_clients.get(username).getStatus() != ClientStatus.Disconnected){
                throw new ClientAlreadyConnectedExeption();
            }
            identified_clients.put(username, client);
        }
    }
    
    private void waitingForIdentification(){
        while(true){
            synchronized (unidentified_clients) {
                for (Client client : unidentified_clients) {
                    Optional<Message> msg;
                    try{
                        msg = client.receive();
                        if(msg.isEmpty()){
                            continue;
                        }
                        if(msg.get() instanceof LogIn){
                            addIdentifiedClient(((LogIn) msg.get()).getUsername(), client);
                            unidentified_clients.remove(client);
                        }
                    }catch (Exception e) {
                        client.disconnect();
                        unidentified_clients.remove(client);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        super.run();
        identification_thread.start();
        while(true){
            try {
                Socket client_socket = socket.accept();
                Client client = new Client(client_socket);
                synchronized (unidentified_clients) {
                    unidentified_clients.add(client);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Client> getClientByUsername(String username){
        synchronized (identified_clients) {
            if(identified_clients.containsKey(username)){
                return Optional.of(identified_clients.get(username));
            }
            return Optional.empty();
        }
    }


}
