package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase extends HashMap<ArrayList<String>, String> {
	private static DataBase instance;
	private static final String DB_NAME = "db.srl";

	private DataBase() {
		super();
		instance = this;
		try {
			instance = loadDb();
		} catch (Exception e) {}
	}
	public static DataBase getInstance() {
		if (instance == null) {
			instance = new DataBase();
		}
		return instance;
	}

	private DataBase loadDb() throws Exception {
		DataBase savedDb;
		synchronized(instance) {
			FileInputStream inputFile = new FileInputStream(DB_NAME);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputFile);
			savedDb = (DataBase)objectInputStream.readObject();
			objectInputStream.close();
			inputFile.close();
		}

		return savedDb;
	}

	public void write() throws Exception {
		synchronized(instance) {
			FileOutputStream outputFile = new FileOutputStream(DB_NAME);
			ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
			outputStream.writeObject(instance);
			outputStream.close();
			outputFile.close();
		}
	}
}
