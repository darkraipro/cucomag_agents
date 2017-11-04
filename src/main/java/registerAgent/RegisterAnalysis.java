package registerAgent;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class RegisterAnalysis {

	private TupleSpace tupleSpace;
	private int session;
	String[] storeConnectionInfo = new String[3];

	public RegisterAnalysis() throws NumberFormatException, TupleSpaceException {
		connectToSQLSpace();
		getTupleSession();
		registerCallback();
	}

	private void getTupleSession() throws TupleSpaceException {
		Tuple sessionTuple = tupleSpace
				.read(new Tuple(new Field[] { new Field("SessionNumber"), new Field(Integer.class) }));

		if (sessionTuple == null) {
			session = 1;
		} else {
			session = Integer.parseInt(sessionTuple.getField(1).getValue().toString());
		}

	}

	private void connectToSQLSpace() throws NumberFormatException, TupleSpaceException {
		String regex = "(Host:\\s)|(Port:\\s)|(SpaceName:\\s)";
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/Connection.txt"))) {
			for (int i = 0; i < storeConnectionInfo.length; i++) {
				String line = scanner.nextLine();
				line = line.replaceFirst(regex, "").trim();
				storeConnectionInfo[i] = line;
			}
			tupleSpace = new TupleSpace(storeConnectionInfo[0], storeConnectionInfo[1], storeConnectionInfo[2]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerCallback() throws TupleSpaceException {
		Tuple template = new Tuple(new Field[] { new Field(String.class), new Field(false), new Field(String.class) });
		Callback callback = new RegisterCallback(tupleSpace, session);

		tupleSpace.eventRegister(Callback.Command.WRITE, template, callback, true, tupleSpace.getSpaces()[0]);
		System.out.println("Active User -EventRegister successful.");
	}
}
