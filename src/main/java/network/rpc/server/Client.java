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
	 *
	 * @author Lorenzo
	 */
	public Client(Socket socket, BiFunction<Call<Serializable>,ClientInterface,Result<Serializable>> handler) throws Exception {
		this.socket = socket;
		this.incomingMessages = new ObjectInputStream(socket.getInputStream());
		this.outcomingMessages = new ObjectOutputStream(socket.getOutputStream());
		this.handler = handler;
		this.statusHandler = new ClientStatusHandler();
	}

	/**
	 * Method that returns the status of the client.
	 * @return The status of the client.
	 *
	 * @author Lorenzo
	 */
	@Override
	public ClientStatus getStatus() {
		return statusHandler.getStatus();
	}

	/**
	 * Method that sets the status of the client by invoking the setStatus method of the statusHandler object with the
	 * provided ClientStatus object.
	 * The statusHandler is responsible for managing and handling the client status.
	 *
	 * @param status The ClientStatus object representing the status to be set for the client.
	 *
	 * @author Lorenzo
	 */
	@Override
	public void setStatus(ClientStatus status) {
		statusHandler.setStatus(status);
	}

	/**
	 * Method that sets the last status of the client by invoking the setStatus method of the statusHandler object with the
	 * provided ClientStatus object.
	 * The statusHandler is responsible for managing and handling the client status.
	 *
	 * @param status The ClientStatus object representing the status to be set for the client.
	 *
	 * @author Marco
	 */
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

	/**
	 * Method that sends a server event to the server by serializing the provided ServerEvent object and writing it to
	 * the outgoing message stream.
	 * The server event represents an event or message sent from the client to the server.
	 *
	 * @param message The ServerEvent object representing the server event to be sent.
	 * @param <T> The type parameter representing the serializable data type associated with the server event.
	 *
	 * @author Lorenzo
	 */
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
	 *
	 * @author Lorenzo
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
	 *
	 * @author Lorenzo
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
	 *
	 * @author Lorenzo
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

	/**
	 * Method that checks if the client is in a disconnected state by comparing the current status of the client with
	 * the ClientStatus.Disconnected value.
	 * It returns a boolean indicating whether the client is disconnected or not.
	 * @return A boolean indicating whether the client is disconnected or not.
	 *
	 * @author Lorenzo
	 */
	@Override
	public boolean isDisconnected(){
		return getStatus() == ClientStatus.Disconnected;
	}

	/**
	 * Method that checks the ping status of the client.
	 * If the client is already disconnected, it returns false.
	 * Otherwise, it synchronizes on the lastMessageTimeLock to ensure thread safety.
	 * It compares the last message time with the current time plus a timeout period.
	 * If the last message time is earlier than the current time plus the timeout, it disconnects the client and returns false.
	 * Otherwise, it returns true indicating that the ping is successful.
	 *
	 * @author Marco
	 *
	 */
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

	/**
	 * Method that represents the main execution loop of the client.
	 * It continuously runs while the client's status is not set to ClientStatus.Disconnected.
	 * The receive method is called to receive a Call object from the server.
	 * The lastMessageTime is updated to the current time, synchronized on the lastMessageTimeLock for thread safety.
	 * If the received call's service is Service.Ping, a response with a Result indicating a successful ping is sent back.
	 * Otherwise, the received call is passed to the handler function along with the client instance to process it.
	 * The resulting Result object is sent back to the server.
	 * If a DisconnectedClientException is caught during the execution, a warning message is logged.
	 *
	 * @author Lorenzo
	 */
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

	/**
	 * Set the handler function of the client.
	 * @param handler The handler function to set.
	 *
	 * @author Lorenzo
	 * */
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
	 *
	 * @author Riccardo
	 */
	protected void setUsername(String username) throws ClientAlreadyIdentifiedException {
		if(this.username != null){
			throw new ClientAlreadyIdentifiedException();
		}
		this.username = username;
	}

	/**
	 * Get the username of the client.
	 * @return The username of the client.
	 * @throws ClientNotIdentifiedException
	 *
	 * @author Riccardo
	 */
	@Override
	public String getUsername() throws ClientNotIdentifiedException {
		if(this.username == null){
			throw new ClientNotIdentifiedException();
		}
		return this.username;
	}

	/**
	 * Get the last message time of the client.
	 * @return The last message time of the client.
	 *
	 * @author Lorenzo
	 */
	@Override
	public LocalDateTime getLastMessageTime(){
		synchronized (lastMessageTimeLock){
			return lastMessageTime;
		}
	}

	/**
	 * Method that retrieves the call handler function responsible for handling incoming calls from the server.
	 * The call handler function is a BiFunction that takes a Call object and a ClientInterface as input and returns a Result object.
	 * The call handler function is synchronized on the handlerLock object to ensure thread safety.
	 * @return handler
	 *
	 * @author Lorenzo
	 */
	@Override
	public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler() {
		synchronized (this.handlerLock) {
			return handler;
		}
	}

	/**
	 * Get the status of the client.
	 *
	 * @author Lorenzo
	 */
	@Override
	public void recoverStatus(){
		statusHandler.setStatus(statusHandler.getLastValidStatus());
	}
}
