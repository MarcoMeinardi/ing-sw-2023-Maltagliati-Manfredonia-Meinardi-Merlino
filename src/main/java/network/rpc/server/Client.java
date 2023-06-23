package network.rpc.server;

import network.*;
import network.errors.ClientAlreadyConnectedException;
import network.errors.ClientNotIdentifiedException;
import network.errors.DisconnectedClientException;
import network.errors.ClientAlreadyIdentifiedException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * Class to represent a client connected to the server via socket.
 */
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
	protected static final int TIMEOUT = 5;

	/**
	 * Constructor of the class.
	 * @param socket Socket of the client.
	 * @param handler Function to handle the calls received from the client.
	 * @throws Exception If an error occurs.
	 */
	public Client(Socket socket, BiFunction<Call<Serializable>,ClientInterface,Result<Serializable>> handler) throws Exception {
		this.socket = socket;
		this.incomingMessages = new ObjectInputStream(socket.getInputStream());
		this.outcomingMessages = new ObjectOutputStream(socket.getOutputStream());
		this.handler = handler;
		this.statusHandler = new ClientStatusHandler();
	}

	@Override
	public ClientStatus getStatus() {
		return statusHandler.getStatus();
	}

	@Override
	public void setStatus(ClientStatus status) {
		statusHandler.setStatus(status);
	}

	@Override
	public void setLastValidStatus(ClientStatus status) {
		statusHandler.setLastValidStatus(status);
	}

	/**
	 * Inherit the status and the call handler from another client.
	 * @param old_client The client to inherit from.
	 */
	public void from(Client old_client){
		setCallHandler(old_client.getCallHandler());
		setStatus(old_client.statusHandler.getLastValidStatus());
	}

	@Override
	public <T extends Serializable> void sendEvent(ServerEvent<T> message){
		synchronized (this.outcomingMessages){
			try{
				outcomingMessages.reset();
				Result<Serializable> result = Result.serverPush(message);
				outcomingMessages.writeObject((Object)result);
			}catch(Exception e){
				Logger.getLogger(Client.class.getName()).warning(e.getMessage());
				disconnect();
			}
		}
	}

	/**
	 * Send a message to the client in response to a call.
	 * @param message The message to send.
	 * @param <T> The type of the message.
	 * @throws DisconnectedClientException If the client is disconnected.
	 */
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

	/**
	 * Wait for a call from the client.
	 * @return The call received.
	 * @param <T> The type call parameters.
	 * @throws DisconnectedClientException If the client is disconnected.
	 */
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

	/**
	 * disconnect the client and close the socket.
	 */
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

	@Override
	public boolean isDisconnected(){
		return getStatus() == ClientStatus.Disconnected;
	}

	@Override
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

	@Override
	public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler){
		synchronized (this.handlerLock) {
			this.handler = handler;
		}
	}

	/**
	 * Set the username of the client.
	 * @param username The username to set.
	 * @throws ClientAlreadyIdentifiedException If the client is already connected and identified.
	 */
	protected void setUsername(String username) throws ClientAlreadyIdentifiedException {
		if(this.username != null){
			throw new ClientAlreadyIdentifiedException();
		}
		this.username = username;
	}

	@Override
	public String getUsername() throws ClientNotIdentifiedException {
		if(this.username == null){
			throw new ClientNotIdentifiedException();
		}
		return this.username;
	}

	@Override
	public LocalDateTime getLastMessageTime(){
		synchronized (lastMessageTimeLock){
			return lastMessageTime;
		}
	}

	@Override
	public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler() {
		synchronized (this.handlerLock) {
			return handler;
		}
	}

	@Override
	public void recoverStatus(){
		statusHandler.setStatus(statusHandler.getLastValidStatus());
	}
}
