package it.polimi.ingsw;

import cli.CLI;

public class Client {
	private static CLI cli = CLI.getInstance();

	static public void main(String[] args) throws Exception {
		cli.run();
	}
}
