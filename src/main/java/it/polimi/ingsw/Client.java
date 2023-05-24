package it.polimi.ingsw;

import view.cli.CLI;

public class Client {
	private static CLI cli = CLI.getInstance();

	static public void main(String[] args) throws Exception {
		CLI.networkManager = network.rmi.client.NetworkManager.getInstance();
		cli.run();
	}
}
