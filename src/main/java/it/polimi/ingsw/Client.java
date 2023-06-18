package it.polimi.ingsw;

import view.cli.CLI;

public class Client {
	private static CLI cli = CLI.getInstance();

	static public void main(String[] args) throws Exception {
		cli.run();
		System.exit(0);
	}
}
