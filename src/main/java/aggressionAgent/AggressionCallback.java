package aggressionAgent;

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

public class AggressionCallback implements Callback {

	List<String> aggroList;
	TupleSpace tupleSpace;
	final static String space = "COLCOMA";
	int aggressionCounter;

	public AggressionCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		loadList();
	}

	private void loadList() {
		aggroList = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("src/main/java/ressources/aggroWords.txt"))) {
			while (scanner.hasNextLine()) {
				aggroList.add(scanner.nextLine().trim().toLowerCase());
			}
			System.out.println("Reading aggroList complete.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		Tuple tuple = afterCmd;
		String name = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).toString();
		String sentenceTippedByUser = tuple.getField(2).toString();
		String wholeStatement = (sentenceOpener + sentenceTippedByUser).trim();
		int sessionID = Integer.parseInt(tuple.getField(3).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		long tupleID = tuple.getTupleID().getID();
		boolean aggressive = false;

		if (!"".equals(name)) {
			Tuple takeTupleIfMatches = new Tuple(new Field[] { new Field(sessionID), new Field("Aggressions-Feedback"),
					new Field(name), new Field(aggressionCounter) });
			Tuple counterTuple;
			try {
				counterTuple = tupleSpace.take(takeTupleIfMatches);
				if (counterTuple != null) {
					aggressionCounter = Integer.parseInt(counterTuple.getField(3).getValue().toString());
					aggressive = false;
				} else {
					aggressionCounter = 0;
					aggressive = false;
				}
			} catch (TupleSpaceException e) {
				e.printStackTrace();
			}
			if (wholeStatement.contains("!!!")) {
				aggressive = true;
			} else if (!aggressive) {
				aggressive = checkAggressivenessOf(wholeStatement);
			} else {
				aggressive = false;
			}
			if (wholeStatement.contains("STARTCONVERSATION")) {
				aggressive = false;
			}
			if (aggressive) {
				aggressionCounter++;
				writeAggressionFeedback(sessionID, name, aggressionCounter);
			} else {
				writeAggressionFeedback(sessionID, name, aggressionCounter);
			}
			writeAggressionAnalysis(sessionID, ID, aggressive, aggressionCounter, tupleID);
		}

	}

	void writeAggressionFeedback(int sessionID, String name, int aggressionCounter) {
		try {
			tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("Aggressions-Feedback"),
					new Field(name), new Field(aggressionCounter) }), space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
	}

	void writeAggressionAnalysis(int sessionID, int ID, boolean aggressive, int aggressionCounter, long tupleID) {
		Tuple tmp = new Tuple(new Field[] { new Field(sessionID), new Field(ID), new Field("Aggression analysis"),
				new Field(aggressive), new Field(aggressionCounter), new Field(tupleID) });
		try {
			tupleSpace.write(tmp, space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		System.out.println("\nAggressiv: " + tmp.getField(3) + "\nCounter: " + tmp.getField(4));
	}

	boolean checkAggressivenessOf(String wholeStatement) {
		int upperCaseCounter = 0;
		for (char c : wholeStatement.toCharArray()) {
			if (Character.isUpperCase(c)) {
				upperCaseCounter++;
			}
		}
		// set arbitrarily threshold: if 2/3 of chars are uppercase
		// aggressive = true
		// -17 because sentenceOpener consists of : <String> XXXX<String>, but
		// -2 because two Uppercase 'S' .
		if (((double) (upperCaseCounter - 2) / (double) (wholeStatement.length() - 17)) > ((double) 2 / (double) 3)) {
			return true;
		}

		for (String s : aggroList) {
			String[] statements = wholeStatement.toLowerCase().split("\\s");
			for (String d : statements) {
				if (d.equals(s) || d.equals(s + ";") || d.equals(s + ".") || d.equals(s + ",") || d.equals(s + "!")) {
					return true;
				}
			}
		}
		return false;
	}
}
