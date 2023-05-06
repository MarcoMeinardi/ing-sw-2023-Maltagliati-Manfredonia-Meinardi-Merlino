package network.rmi;
import network.Call;
import network.Result;
import network.ServerEvent;

import java.rmi.Remote;

public interface ClientService extends Remote {
    Result requestService(Call call);
    ServerEvent pollEvent();
}
