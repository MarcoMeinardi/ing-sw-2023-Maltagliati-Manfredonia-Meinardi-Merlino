package network.rmi;
import network.Call;
import network.Result;
import network.ServerEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    Result requestService(Call call) throws RemoteException;
    ServerEvent pollEvent() throws RemoteException;
    Boolean hasEvent() throws  RemoteException;
}
