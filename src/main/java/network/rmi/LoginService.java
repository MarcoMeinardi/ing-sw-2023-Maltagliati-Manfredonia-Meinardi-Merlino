package network.rmi;

import network.Result;
import network.parameters.Login;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface LoginService extends Remote {
    public Result<Serializable> login(Login info, UUID CallId) throws RemoteException, Exception;
}
