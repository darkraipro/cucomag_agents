package stepAgent;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class StepsCallback implements Callback {

	TupleSpace tupleSpace;
	public final static String space = "COLCOMA";

	public StepsCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
	}

	// Eingangstuple: (Name | Sentenceopener | Anfrage | Session# | Phase | ID )
	// (String | String | String | int | int | int )

	@Override
	public void call(Command cmd, int seqnum, Tuple afterCmd, Tuple beforeCmd) {
		Tuple tuple = afterCmd;
		String name = tuple.getField(0).getValue().toString();
		int sessionID = Integer.parseInt(tuple.getField(3).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		long tupleID = tuple.getTupleID().getID();
		int stepCounter = 0;

		Tuple counterTuple;
		try {
			counterTuple = tupleSpace.take(new Tuple(new Field[] { new Field(sessionID), new Field("Steps-Feedback"),
					new Field(name), new Field(Integer.class) }));
			if (counterTuple != null) {
				stepCounter = (int) counterTuple.getField(3).getValue();
			} else {
				stepCounter = 0;
			}
		} catch (TupleSpaceException e1) {
			e1.printStackTrace();
		}
		stepCounter++;

		try {
			tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("Steps-Feedback"), new Field(name),
					new Field(stepCounter) }),space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}

		Tuple tmp = new Tuple(new Field[] { new Field(sessionID), new Field(ID), new Field("Step analyse"),
				new Field(stepCounter), new Field(tupleID) });
		try {
			tupleSpace.write(tmp,space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		System.out.println( "\nStepCounter: " + tmp.getField(3)
		+ "\nSessionNr: " + sessionID);
	}

}
