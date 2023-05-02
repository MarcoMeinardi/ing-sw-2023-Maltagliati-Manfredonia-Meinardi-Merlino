package it.polimi.ingsw;

import controller.lobby.NotEnoughPlayersException;
import network.rpc.server.ClientManager;

public class Server {
	private static ClientManager clientManager = ClientManager.getInstance();
	private static NotEnoughPlayersException.LobbyController lobbyController = NotEnoughPlayersException.LobbyController.getInstance();

	static public void main(String[] args) throws InterruptedException {
		clientManager.join();
	}
}
