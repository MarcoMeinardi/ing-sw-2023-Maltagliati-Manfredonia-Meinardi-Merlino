package it.polimi.ingsw;

import network.rpc.client.NetworkManager;
import network.rpc.client.Server;

public class Client {
	static private NetworkManager networkManager = NetworkManager.getInstance();

	static public void main(String[] args) throws Exception {
		networkManager.connect(new Server("127.0.0.1", 8000));
	}
}