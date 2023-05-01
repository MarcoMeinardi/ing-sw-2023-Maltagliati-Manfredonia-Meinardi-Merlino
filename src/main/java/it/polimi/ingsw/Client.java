package it.polimi.ingsw;

import CLI.CLI;

public class Client {
	static private CLI cli = CLI.getInstance();

	static public void main(String[] args) throws Exception {
		cli.run();
	}
}
