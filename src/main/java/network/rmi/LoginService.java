package network.rmi;

import network.parameters.Login;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginService extends Remote {
    public boolean login(Login info) throws RemoteException;
}
