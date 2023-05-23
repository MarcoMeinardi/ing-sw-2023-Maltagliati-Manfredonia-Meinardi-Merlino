package network;

import network.errors.ClientNotIdentifiedException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

public interface ClientInterface {
    public ClientStatus getStatus();
    public void setStatus(ClientStatus status);
    public <T extends Serializable> void sendEvent(ServerEvent<T> message);
    public boolean isDisconnected();
    public void setCallHandler(BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> handler);
    public String getUsername() throws ClientNotIdentifiedException;
    public LocalDateTime getLastMessageTime();
    public BiFunction<Call<Serializable>, ClientInterface, Result<Serializable>> getCallHandler();

    public boolean checkPing();
}
