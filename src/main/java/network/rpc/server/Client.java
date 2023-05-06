package network.rpc.server;

import network.*;
import network.errors.ClientAlreadyConnectedExeption;
import network.errors.ClientNotIdentifiedException;
import network.errors.DisconnectedClientException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class Client extends Thread implements ClientInterface {
        private final Socket socket;
        private final ObjectInputStream incomingMessages;
        private final ObjectOutputStream outcomingMessages;
        private ClientStatusHandler statusHandler;
        private BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler;
        private Object handlerLock = new Object();
        private LocalDateTime lastMessageTime = LocalDateTime.now();
        private Object lastMessageTimeLock = new Object();
        private String username = null;
        protected static final int TIMEOUT = 60;

        public Client(Socket socket, BiFunction<Call<Serializable>,ClientInterface,Result<Serializable>> handler) throws Exception {
            this.socket = socket;
            this.incomingMessages = new ObjectInputStream(socket.getInputStream());
            this.outcomingMessages = new ObjectOutputStream(socket.getOutputStream());
            this.handler = handler;
            this.statusHandler = new ClientStatusHandler();
        }

        public ClientStatus getStatus() {
            return statusHandler.getStatus();
        }

        public void setStatus(ClientStatus status) {
            statusHandler.setStatus(status);
        }

        public ClientStatus getLastValidStatus() {
            return statusHandler.getLastValidStatus();
        }

        public void setLastValidStatus(ClientStatus status) {
            statusHandler.setLastValidStatus(status);
        }

        public <T extends Serializable> void send(ServerEvent<T> message) throws DisconnectedClientException {
            if(getStatus() == ClientStatus.Disconnected){
                throw new DisconnectedClientException();
            }
            synchronized (this.outcomingMessages){
                try{
                    outcomingMessages.reset();
                    Result<Serializable> result = Result.serverPush(message);
                    outcomingMessages.writeObject((Object)result);
                }catch(Exception e){
                    Logger.getLogger(Client.class.getName()).warning(e.getMessage());
                    disconnect();
                    throw new DisconnectedClientException();
                }
            }
        }

    private <T extends Serializable> void send(Result<T> message) throws DisconnectedClientException {
        if(getStatus() == ClientStatus.Disconnected){
            throw new DisconnectedClientException();
        }
        synchronized (this.outcomingMessages){
            try{
                outcomingMessages.reset();
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
        
        public boolean checkPing() {
            if(getStatus() == ClientStatus.Disconnected){
                return false;
            }
            synchronized (lastMessageTimeLock) {
                if(lastMessageTime.plusSeconds(TIMEOUT).isBefore(LocalDateTime.now())){
                    disconnect();
                    return false;
                }
            }
            return true;
        }

        @Override
        public void run() {
            while (getStatus() != ClientStatus.Disconnected) {
                try {
                    Call call = receive();
                    synchronized (lastMessageTimeLock){
                        lastMessageTime = LocalDateTime.now();
                    }
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

        public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler){
            synchronized (this.handlerLock) {
                this.handler = handler;
            }
        }

        protected void setUsername(String username) throws ClientAlreadyConnectedExeption {
            if(this.username != null){
                throw new ClientAlreadyConnectedExeption();
            }
            this.username = username;
        }

        public String getUsername() throws ClientNotIdentifiedException {
            if(this.username == null){
                throw new ClientNotIdentifiedException();
            }
            return this.username;
        }

        public LocalDateTime getLastMessageTime(){
            synchronized (lastMessageTimeLock){
                return lastMessageTime;
            }
        }

    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler() {
        synchronized (this.handlerLock) {
            return handler;
        }
    }
}
