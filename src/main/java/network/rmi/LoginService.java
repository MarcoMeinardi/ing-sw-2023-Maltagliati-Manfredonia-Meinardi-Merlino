package network.rmi;

import network.Result;
import network.parameters.Login;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * RMI interface for login service
 */
public interface LoginService extends Remote {
    /**
     * Login to the server
     * @param info Login information
     * @param CallId CallId of the request
     * @return Result of the request
     * @throws RemoteException RMI exception
     * @throws Exception Other exceptions
     */
    public Result<Serializable> login(Login info, UUID CallId) throws RemoteException, Exception;
}
