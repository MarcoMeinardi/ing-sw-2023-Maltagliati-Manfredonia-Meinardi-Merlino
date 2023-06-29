package it.polimi.ingsw;

import view.cli.CLI;
import view.gui.Main;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Client {

	public static void main(String[] args) {
		Logger root = Logger.getLogger("");
		root.setLevel(Level.OFF);
		if (Arrays.asList(args).contains("-cli")) {
			CLI cli = CLI.getInstance();
			cli.run();
		} else {
			Main.main(args);
		}
		System.exit(0);
	}
}
