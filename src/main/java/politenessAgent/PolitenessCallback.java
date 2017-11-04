package politenessAgent;

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

public class PolitenessCallback implements Callback {
	TupleSpace tupleSpace;
	List<String> politeList;
	public final static String space = "COLCOMA";
	int politeCounter;

	public PolitenessCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		loadList();
	}

	private void loadList() {
		politeList = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/PoliteWords.txt"))) {
			while (scanner.hasNextLine()) {
				politeList.add(scanner.nextLine().toLowerCase().trim());
			}
			System.out.println("Reading list complete.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		// Eingangstuple: (Name|SentenceOpener|Anfrage|Session#|Phase|ID )
		Tuple tuple = afterCmd;
		String name = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).toString().toLowerCase();
		String sentenceTippedByUser = tuple.getField(2).toString().toLowerCase();
		String wholeStatement = (sentenceOpener + sentenceTippedByUser).trim();
		int sessionID = Integer.parseInt(tuple.getField(3).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		long tupleID = tuple.getTupleID().getID();
		boolean polite = false;
		
		if (!"".equals(name)) {
			Tuple takeTupleIfMatches = new Tuple(new Field[] { new Field(sessionID), new Field("Polite-Feedback"),
					new Field(name), new Field(politeCounter) });
			Tuple counterTuple;
			try {
				counterTuple = tupleSpace.take(takeTupleIfMatches);
				if (counterTuple != null) {
					politeCounter = Integer.parseInt(counterTuple.getField(3).getValue().toString());
				}else{
					politeCounter = 0;
				}
			} catch (TupleSpaceException e) {
				e.printStackTrace();
			}
			for (String s : politeList) {
				if (wholeStatement.contains(s)) {
					polite = true;
					politeCounter++;
					try {
						tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("Polite-Feedback"),
								new Field(name), new Field(politeCounter) }), space);
						break;
					} catch (TupleSpaceException e) {
						e.printStackTrace();
						break;
					}
				}
			}
			if (!polite) {
				try {
					tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("Polite-Feedback"),
							new Field(name), new Field(politeCounter) }), space);
				} catch (TupleSpaceException e) {
					e.printStackTrace();
				}
			}
		}
		Tuple tmp = new Tuple(new Field[] { new Field(sessionID), new Field(ID), new Field("Politeness analysis"),
				new Field(polite), new Field(politeCounter), new Field(tupleID) });
		try {
			tupleSpace.write(tmp, space);
			System.out.println("\nPolite: " + tmp.getField(3) + "\nCounter: " + tmp.getField(4));
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		System.out.println("Politeness-call finished.");
	}

}
