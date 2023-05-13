package it.polimi.ingsw;

import controller.lobby.LobbyController;
import network.ClientManagerInterface;
import network.GlobalClientManager;

public class Server {

	static public void main(String[] args) throws Exception {
		ClientManagerInterface clientManager = GlobalClientManager.getInstance();
		LobbyController lobbyController = LobbyController.getInstance();
		clientManager.waitAndClose();
	}
}
