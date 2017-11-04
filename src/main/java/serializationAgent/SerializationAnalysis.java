package serializationAgent;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class SerializationAnalysis {
	TupleSpace tupleSpace;
	int sessionNumber;
	String[] storeConnectionInfo = new String[3];

	public SerializationAnalysis() throws TupleSpaceException {
		connectToSQLSpace();
		getSessionNumber();
		registerCallback();
	}

	private void connectToSQLSpace() throws TupleSpaceException {
		String regex = "(Host:\\s)|(Port:\\s)|(SpaceName:\\s)";
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/Connection.txt"))) {
			for (int i = 0; i < storeConnectionInfo.length; i++) {
				String line = scanner.nextLine();
				line = line.replaceFirst(regex, "").trim();
				storeConnectionInfo[i] = line;
			}
			tupleSpace = new TupleSpace(storeConnectionInfo[0], storeConnectionInfo[1], storeConnectionInfo[2]);
			System.out.println("SerializationAgent connected to TupleSpace.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getSessionNumber() throws TupleSpaceException {
		Tuple sessionTuple = tupleSpace
				.read(new Tuple(new Field[] { new Field("SessionNumber"), new Field(Integer.class) }));

		if (sessionTuple == null) {
			sessionNumber = 1;
		} else {
			sessionNumber = Integer.parseInt(sessionTuple.getField(1).getValue().toString());
		}
	}

	private void registerCallback() throws TupleSpaceException {
		Tuple template = new Tuple(new Field[] { new Field(String.class), new Field(String.class),
				new Field(String.class), new Field(Integer.class), new Field(Integer.class), new Field(Integer.class),
				new Field(Integer.class), new Field(Boolean.class), new Field(Integer.class), new Field(Boolean.class),
				new Field(Integer.class), new Field(String.class), new Field(Boolean.class), new Field(Integer.class),
				new Field(Boolean.class), new Field(Integer.class), new Field(Integer.class), new Field(Long.class),
				new Field(Boolean.class), new Field(String.class), new Field(Integer.class), new Field(String.class), });
		Callback callback = new SerializationCallback(tupleSpace);
		tupleSpace.eventRegister(Callback.Command.WRITE, template, callback, true, tupleSpace.getSpaces()[0]);
		System.out.println("Serialization Eventregister successful!");
	}

}
