package it.polimi.ingsw;

import controller.lobby.LobbyController;
import network.rpc.server.ClientManager;

public class Server {
	static private ClientManager clientManager = ClientManager.getInstance();
	static private LobbyController lobbyController = LobbyController.getInstance();

	static public void main(String[] args) throws InterruptedException {
		clientManager.join();
	}
}
