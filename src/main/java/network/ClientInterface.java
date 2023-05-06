package network;

import network.errors.ClientAlreadyConnectedExeption;
import network.errors.ClientNotIdentifiedException;
import network.errors.DisconnectedClientException;
import network.rpc.server.Client;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

public interface ClientInterface {
    public ClientStatus getStatus();
    public void setStatus(ClientStatus status);
    public ClientStatus getLastValidStatus();
    public <T extends Serializable> void send(ServerEvent<T> message) throws Exception;
    public boolean isDisconnected();
    public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler);
    public String getUsername() throws ClientNotIdentifiedException;
    public LocalDateTime getLastMessageTime();
    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler();
}
