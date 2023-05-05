package network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginService extends Remote {
    public boolean login(String username) throws RemoteException;
}
