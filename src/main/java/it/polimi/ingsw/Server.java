package it.polimi.ingsw;

import controller.lobby.LobbyController;
import network.ClientManagerInterface;
import network.rpc.server.ClientManager;

public class Server {

	static public void main(String[] args) throws Exception {
		ClientManagerInterface clientManager = ClientManager.getInstance();
		LobbyController lobbyController = LobbyController.getInstance();
		clientManager.waitAndClose();
	}
}
