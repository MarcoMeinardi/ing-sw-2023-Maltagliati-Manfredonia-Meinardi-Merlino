package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase extends HashMap<ArrayList<String>, File> {
	private static DataBase instance;
	private static final String DB_NAME = "db.srl";

	private DataBase() {
		super();
		try {
			instance = loadDb();
		} catch (Exception e) {
			instance = this;
		}
	}
	public static DataBase getInstance() {
		if (instance == null) {
			instance = new DataBase();
		}
		return instance;
	}

	private DataBase loadDb() throws IOException, ClassNotFoundException {
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
