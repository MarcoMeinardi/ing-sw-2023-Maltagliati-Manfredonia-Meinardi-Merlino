package network.rpc.server;

import network.rpc.Call;
import network.rpc.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Optional;

public class Client {
        public final int TIMEOUT = 30;
        private final Socket socket;
        private final ObjectInputStream incomingMessages;
        private final ObjectOutputStream outcomingMessages;
        private ClientStatus status;

        public Client(Socket socket) throws Exception {
            this.status = ClientStatus.Disconnected;
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

        public <T extends Serializable> void send(Result<T> message) throws DisconnectedClientException {
            if(getStatus() == ClientStatus.Disconnected){
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

        public <T extends Serializable> Optional<Call<T>> receive() throws DisconnectedClientException{
            if(getStatus() == ClientStatus.Disconnected){
                throw new DisconnectedClientException();
            }
            synchronized (this.incomingMessages){
                try{
                    if(this.incomingMessages.available() == 0){
                        return Optional.empty();
                    }
                    Object obj = this.incomingMessages.readObject();
                    if(obj instanceof Call){
                        return Optional.of((Call)obj);
                    }
                }catch(Exception e){}
                disconnect();
                throw new DisconnectedClientException();
            }
        }

        public void disconnect(){
            setStatus(ClientStatus.Disconnected);
            synchronized (socket){
                try{
                    socket.close();
                }catch (IOException exception){}
            }
        }

}