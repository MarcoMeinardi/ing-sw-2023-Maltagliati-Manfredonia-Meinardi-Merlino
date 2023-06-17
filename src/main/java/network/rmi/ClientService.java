package network.rmi;
import network.Call;
import network.Result;
import network.ServerEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI service that is exposed to the client
 */
public interface ClientService extends Remote {

    /**
     * Request a service from the server
     * @param call the call to make
     * @return the result of the call
     * @throws RemoteException if rmi fails
     */
    Result requestService(Call call) throws RemoteException;

    /**
     * Poll an event from the server
     * @return the event if there is one, null otherwise
     * @throws RemoteException if rmi fails
     */
    ServerEvent pollEvent() throws RemoteException;

    /**
     * Check if there is an event
     * @return true if there is an event, false otherwise
     * @throws RemoteException if rmi fails
     */
    Boolean hasEvent() throws  RemoteException;
}
