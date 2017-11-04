package aggressionAgent;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class AggressionAnalysis {
	
	String[] storeConnectionInfo = new String[3];
	TupleSpace tupleSpace;

	public AggressionAnalysis() throws NumberFormatException, TupleSpaceException{
		connectToSQLSpace();
		registerCallback();
	}
	
	private void connectToSQLSpace() throws NumberFormatException, TupleSpaceException {
		String regex = "(Host:\\s)|(Port:\\s)|(SpaceName:\\s)";
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/Connection.txt"))) {
			for (int i = 0; i < storeConnectionInfo.length; i++) {
				String line = scanner.nextLine();
				line = line.replaceFirst(regex, "").trim();
				storeConnectionInfo[i] = line;
			}
			tupleSpace = new TupleSpace(storeConnectionInfo[0],storeConnectionInfo[1],storeConnectionInfo[2]);
			System.out.println("AggressionAgent connected to TupleSpace.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerCallback() throws TupleSpaceException {
		Tuple template = new Tuple(
				new Field[] { new Field(String.class), new Field(String.class), new Field(String.class),
						new Field(Integer.class), new Field(Integer.class), new Field(Integer.class) });
		Callback callback = new AggressionCallback(tupleSpace);
		
		tupleSpace.eventRegister(Callback.Command.WRITE, template, callback, true, tupleSpace.getSpaces()[0]);
		System.out.println("Aggression-EventRegister succesful.");
	}
}
