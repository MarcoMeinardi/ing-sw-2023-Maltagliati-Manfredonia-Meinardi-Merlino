package network.socket.serverSide;
import network.socket.protoGabibbo.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.function.Function;

public class Client {
    public final int TIMEOUT = 30;
    private final Socket socket;
    private final ObjectInputStream incomingMessages;
    private final ObjectOutputStream outcomingMessages;
    private ClientStatus status;

    public Client(Socket socket) throws Exception {
        this.status = ClientStatus.Disconnect;
        this.socket = socket;
        this.socket.setSoTimeout(TIMEOUT);
        this.incomingMessages = new ObjectInputStream(socket.getInputStream());
        this.outcomingMessages = new ObjectOutputStream(socket.getOutputStream());
        this.status = ClientStatus.Idle;
    }

    public ClientStatus getStatus() {
        synchronized (this.status){
            return this.status;
        }
    }

    public void setStatus(ClientStatus status) {
        synchronized (this.status){
            this.status = status;
        }
    }

    public void send(Message message) throws DisconnectedClientException{
        if(getStatus() == ClientStatus.Disconnect){
            throw new DisconnectedClientException();
        }
        synchronized (this.outcomingMessages){
            try{
                outcomingMessages.writeObject((Object)message);
            }catch(Exception e){
                disconnect();
                throw new DisconnectedClientException();
            }
        }
    }

    public Message receive() throws DisconnectedClientException{
        if(getStatus() == ClientStatus.Disconnect){
            throw new DisconnectedClientException();
        }
        synchronized (this.incomingMessages){
            try{
                Object obj = this.incomingMessages.readObject();
                if(obj instanceof Message){
                    return (Message)obj;
                }

            }catch(Exception e){}
            disconnect();
            throw new DisconnectedClientException();
        }
    }

    public void disconnect(){
        setStatus(ClientStatus.Disconnect);
        synchronized (socket){
            try{
                socket.close();
            }catch (IOException exception){}
        }
    }
}
