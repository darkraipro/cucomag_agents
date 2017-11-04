package rudenessAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class RudenessCallback implements Callback {
	TupleSpace tupleSpace;
	List<String> insultList;
	public static final String space = "COLCOMA";
	int rudenessCounter;

	public RudenessCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		loadList();
	}

	private void loadList() {
		insultList = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/Insults.txt"))) {
			while (scanner.hasNextLine()) {
				insultList.add(scanner.nextLine().toLowerCase().trim());
			}
			System.out.println("Loading insult List complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {

		Tuple tuple = afterCmd;
		String name = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).toString().toLowerCase();
		String sentenceTippedByUser = tuple.getField(2).toString().toLowerCase();
		String wholeStatement = (sentenceOpener + sentenceTippedByUser).trim().toLowerCase();
		int sessionID = Integer.parseInt(tuple.getField(3).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		long tupleID = tuple.getTupleID().getID();
		boolean rudeness = false;

		if (!"".equals(name)) {
			Tuple takeTupleIfMatches = new Tuple(new Field[] { new Field(sessionID), new Field("Schimpf-Feedback"),
					new Field(name), new Field(rudenessCounter) });
			Tuple counterTuple;
			try {
				counterTuple = tupleSpace.take(takeTupleIfMatches);
				if (counterTuple != null) {
					rudenessCounter = Integer.parseInt(counterTuple.getField(3).getValue().toString());
				} else {
					rudenessCounter = 0;
				}
			} catch (TupleSpaceException e) {
				e.printStackTrace();
			}
			for (String s : insultList) {
				String[] contains = wholeStatement.split("\\s");
				for (String d : contains) {
					if (d.equals(s) || d.equals(s + ";") || d.equals(s + ".") || d.equals(s + ",") || d.equals(s + "!")) {
						rudeness = true;
						rudenessCounter++;
						try {
							tupleSpace.write(new Tuple(new Field[] { new Field(sessionID),
									new Field("Schimpf-Feedback"), new Field(name), new Field(rudenessCounter) }),
									space);
							break;
						} catch (TupleSpaceException e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
			if (!rudeness) {
				try {
					tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("Schimpf-Feedback"),
							new Field(name), new Field(rudenessCounter) }), space);
				} catch (TupleSpaceException e) {
					e.printStackTrace();
				}
			}
		}
		Tuple tmp = new Tuple(new Field[] { new Field(sessionID), new Field(ID), new Field("Rudeness analysis"),
				new Field(rudeness), new Field(rudenessCounter), new Field(tupleID) });
		try {
			tupleSpace.write(tmp, space);
			System.out.println("\nRudeness: " + tmp.getField(3) + "\nrudenessCounter: " + tmp.getField(4)
			+ "\nSessionNr: " + sessionID);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		System.out.println("Rudeness-call finished.");
	}

}
