package network.rpc.server;

import network.rpc.Call;
import network.rpc.Result;
import network.rpc.ServerEvent;
import network.rpc.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class Client extends Thread{
        public final int TIMEOUT = 30;
        private final Socket socket;
        private final ObjectInputStream incomingMessages;
        private final ObjectOutputStream outcomingMessages;
        private ClientStatus status;
        private BiFunction<Call<Serializable>, Client, Result<Serializable>> handler;

        private String username = null;

        public Client(Socket socket, BiFunction<Call<Serializable>,Client,Result<Serializable>> handler) throws Exception {
            this.status = ClientStatus.Disconnected;
            this.socket = socket;
            this.socket.setSoTimeout(TIMEOUT);
            this.incomingMessages = new ObjectInputStream(socket.getInputStream());
            this.outcomingMessages = new ObjectOutputStream(socket.getOutputStream());
            this.status = ClientStatus.Idle;
            this.handler = handler;
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
                    Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                    disconnect();
                    throw new DisconnectedClientException();
                }
            }
        }

        private <T extends Serializable> Call<T> receive() throws DisconnectedClientException{
            if(getStatus() == ClientStatus.Disconnected){
                throw new DisconnectedClientException();
            }
            synchronized (this.incomingMessages){
                try{
                    Object obj = this.incomingMessages.readObject();
                    if(obj instanceof Call){
                        return (Call)obj;
                    }
                }catch(Exception e){
                    Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                }
                disconnect();
                throw new DisconnectedClientException();
            }
        }

        public void disconnect(){
            setStatus(ClientStatus.Disconnected);
            synchronized (socket){
                try{
                    socket.close();
                }catch (IOException e){
                    Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                }
            }
        }

        public boolean isDisconnected(){
            return getStatus() == ClientStatus.Disconnected;
        }


        @Override
        public void run() {
            while (getStatus() != ClientStatus.Disconnected) {
                try {
                    Call call = receive();
                    if(call.service() == Service.Ping){
                        send(Result.ok(true, call.id()));
                    }else{
                        Result result = handler.apply(call, this);
                        send(result);
                    }
                } catch (DisconnectedClientException e) {
                    Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                }
            }
        }

        public void setCallHandler(BiFunction<Call<Serializable>, Client, Result<Serializable>> handler){
            this.handler = handler;
        }

        protected void setUsername(String username) throws ClientAlreadyConnectedExeption{
            if(this.username != null){
                throw new ClientAlreadyConnectedExeption();
            }
            this.username = username;
        }

        public String getUsername() throws ClientNotIdentifiedException{
            if(this.username == null){
                throw new ClientNotIdentifiedException();
            }
            return this.username;
        }
}