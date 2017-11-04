package collectingAgent;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class CollectingCallback implements Callback {

	TupleSpace tupleSpace;
	static final String space = "COLCOMA";

	public CollectingCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		Tuple tuple = afterCmd;
		long tupleID = tuple.getTupleID().getID();
		String name = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).getValue().toString();
		String request = tuple.getField(2).getValue().toString();
		int session = Integer.parseInt(tuple.getField(3).getValue().toString());
		int phase = Integer.parseInt(tuple.getField(4).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());

		Tuple rudenessTuple = null;
		Tuple aggressionTuple = null;
		Tuple emotionTuple = null;
		Tuple themeTuple = null;
		Tuple politeTuple = null;
		Tuple nogoTuple = null;
		Tuple stepTuple = null;
		Tuple messageTimeTuple = null;
		Tuple endingTuple = null; // Quality of Ending

		try {
			messageTimeTuple = tupleSpace
					.waitToTake(
							new Tuple(new Field[] { new Field(name), new Field(session), new Field(ID),
									new Field("MessageTimeAnalysis"), new Field(Integer.class) }),
							200, new String[] { space });

			int messageTime = messageTimeTuple != null
					? Integer.parseInt(messageTimeTuple.getField(4).getValue().toString()) : 0;

			stepTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field(ID), new Field("Step analyse"),
							new Field(Boolean.class), new Field(Integer.class), new Field(tupleID) }),
					200, new String[] { space });
			int stepCounter = stepTuple != null ? Integer.parseInt(stepTuple.getField(3).getValue().toString()) : 0;

			nogoTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field(ID), new Field("NoGo analysis"),
							new Field(Boolean.class), new Field(Integer.class), new Field(tupleID) }),
					200, new String[] { space });
			boolean nogo = nogoTuple != null ? (boolean) nogoTuple.getField(3).getValue() : false;
			int nogoCounter = nogoTuple != null ? (int) nogoTuple.getField(4).getValue() : 0;

			rudenessTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field(ID), new Field("Rudeness analysis"),
							new Field(Boolean.class), new Field(Integer.class), new Field(tupleID) }),
					200, new String[] { space });
			boolean rudeness = rudenessTuple != null ? (boolean) rudenessTuple.getField(3).getValue() : false;
			int rudenessCounter = rudenessTuple != null ? (int) rudenessTuple.getField(4).getValue() : 0;

			politeTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field(ID), new Field("Politeness analysis"),
							new Field(Boolean.class), new Field(Integer.class), new Field(tupleID) }),
					200, new String[] { space });
			boolean politeness = politeTuple != null ? (boolean) politeTuple.getField(3).getValue() : false;
			int politenessCounter = politeTuple != null ? (int) politeTuple.getField(4).getValue() : 0;

			aggressionTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field(ID), new Field("Aggression analysis"),
							new Field(Boolean.class), new Field(Integer.class), new Field(tupleID) }),
					200, new String[] { space });
			boolean aggression = aggressionTuple != null ? (boolean) aggressionTuple.getField(3).getValue() : false;
			int aggressionCounter = aggressionTuple != null ? (int) aggressionTuple.getField(4).getValue() : 0;

			emotionTuple = tupleSpace.waitToTake(new Tuple(new Field[] { new Field(session), new Field(ID),
					new Field("Visual analysis"), new Field(Integer.class), new Field(tupleID) }), 200,
					new String[] { space });
			int emotion = emotionTuple != null ? (int) emotionTuple.getField(3).getValue() : 0;

			themeTuple = tupleSpace.waitToTake(
					new Tuple(
							new Field[] { new Field(session), new Field("Themes analysis"), new Field(String.class) }),
					200, new String[] { space });
			String theme = themeTuple != null ? themeTuple.getField(2).getValue().toString() : "";

			endingTuple = tupleSpace.waitToTake(
					new Tuple(new Field[] { new Field(session), new Field("Ending"), new Field(String.class) }), 200,
					new String[] { space });

			String endingquality = endingTuple != null ? endingTuple.getField(2).getValue().toString() : "unknown";

			Tuple tmp = new Tuple(new Field[] { new Field(name), new Field(sentenceOpener), new Field(request),
					new Field(session), new Field(phase), new Field(ID), new Field(emotion), new Field(rudeness),
					new Field(rudenessCounter), new Field(aggression), new Field(aggressionCounter), new Field(theme),
					new Field(politeness), new Field(politenessCounter), new Field(nogo), new Field(nogoCounter),
					new Field(stepCounter), new Field((long) messageTime), new Field(false),
					new Field(endingquality) });
			tupleSpace.write(tmp, space);
			System.out.println("Endsolution quality: " + endingquality);
			System.out.println("Collecting tuples and forwarding done.");
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
	}

}
