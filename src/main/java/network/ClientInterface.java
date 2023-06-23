package network;

import network.errors.ClientNotIdentifiedException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

/**
 * Interface for the client object used in the server to handle communication with the client
 */
public interface ClientInterface {
    /**
     * Get the client status
     * @return the client status
     */
    public ClientStatus getStatus();
    /**
     * Set the client status
     * @param status the new client status
     */
    public void setStatus(ClientStatus status);

    /**
     * Set the last valid status of the client (the status that is different from disconnected)
     * @param status the last valid status of the client
     */
    public void setLastValidStatus(ClientStatus status);

    /**
     * Send and event to the client
     * @param message The event to send
     * @param <T> The type of the event
     */
    public <T extends Serializable> void sendEvent(ServerEvent<T> message);

    /**
     * Check if the client is disconnected
     * @return true if the client is disconnected, false otherwise
     */
    public boolean isDisconnected();

    /**
     * Set the call handler for the client
     * @param handler the call handler to set
     */
    public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler);

    /**
     * Get the username of the client
     * @return the username of the client
     * @throws ClientNotIdentifiedException if the client has not completed the login yet
     */
    public String getUsername() throws ClientNotIdentifiedException;

    /**
     * Get the time of the last message received from the client
     * @return the time of the last message received from the client
     */
    public LocalDateTime getLastMessageTime();

    /**
     * Get the call handler for the client
     * @return the call handler for the client
     */
    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler();

    /**
     * Check if the client is still connected
     * @return true if the client is still connected, false otherwise
     */
    public boolean checkPing();

    /**
     * Set Status to last valid status
     */
    public void recoverStatus();
}
