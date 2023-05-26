package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A minimal db to associate a set of players to their save file.
 * 
 * @author Marco
 */
public class DataBase extends HashMap<HashSet<String>, File> {
	private static DataBase instance = null;
	private static final String DB_NAME = "db.srl";

	/**
	 * Private constructor that loads the database from the file "db.srl" if it exists, or creates a new database otherwise.
	 * 
	 * @author Marco
	 */
	private DataBase() {
		super();
		try {
			instance = loadDb();
		} catch (Exception e) {
			instance = this;
		}
	}
	/**
	 * Returns the instance of the database. If the database has not been created yet, it creates a new instance.
	 * 
	 * @author Marco
	 * @return the instance of the database
	 */
	public static DataBase getInstance() {
		if (instance == null) {
			new DataBase();
		}
		return instance;
	}

	/**
	 * Loads the database from the file "db.srl".
	 * 
	 * @author Marco
	 * @return the loaded database
	 * @throws IOException if an I/O error occurs while reading the file
	 * @throws ClassNotFoundException if the class of the serialized object cannot be found
	 */
	private DataBase loadDb() throws IOException, ClassNotFoundException {
		DataBase savedDb;
		FileInputStream inputFile = new FileInputStream(DB_NAME);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputFile);
		savedDb = (DataBase)objectInputStream.readObject();
		objectInputStream.close();
		inputFile.close();

		return savedDb;
	}

	/**
	 * Writes the database to the file "db.srl".
	 * 
	 * @author Marco
	 * @throws IOException if an I/O error occurs while writing the file
	 */
	public void write() throws IOException {
		synchronized(instance) {
			FileOutputStream outputFile = new FileOutputStream(DB_NAME);
			ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
			outputStream.writeObject(instance);
			outputStream.close();
			outputFile.close();
		}
	}
}
