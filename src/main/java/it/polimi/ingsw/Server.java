package it.polimi.ingsw;

import controller.lobby.LobbyController;
import network.ClientManagerInterface;
import network.rpc.server.ClientManager;

public class Server {
	private static ClientManagerInterface clientManager = ClientManager.getInstance();
	private static LobbyController lobbyController = LobbyController.getInstance();

	static public void main(String[] args) throws InterruptedException {
		clientManager.waitAndClose();
	}
}
