package scoreAgent;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class ScoreCallback implements Callback {

	final static String space = "COLCOMA";
	TupleSpace tupleSpace;
	int[] score;

	public ScoreCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		this.score = new int[99];
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		try {
			Tuple tuple = tupleSpace.read(afterCmd);
			String playerName = tuple.getField(0).getValue().toString();

			String sentenceOpener = tuple.getField(1).getValue().toString();
			String requestText = tuple.getField(2).getValue().toString();
			String requestSentence = sentenceOpener + requestText;
			int session = Integer.parseInt(tuple.getField(3).getValue().toString());
			int answerquality = Integer.parseInt(tuple.getField(4).getValue().toString());
			int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
			int emotion = Integer.parseInt(tuple.getField(6).getValue().toString());
			boolean geschimpft = (boolean) tuple.getField(7).getValue();
			int swearwordCounter = Integer.parseInt(tuple.getField(8).getValue().toString());
			boolean aggressiv = (boolean) tuple.getField(9).getValue();
			int aggressionCounter = Integer.parseInt(tuple.getField(10).getValue().toString());
			String theme = tuple.getField(11).getValue().toString();
			boolean polite = (boolean) tuple.getField(12).getValue();
			int politenesscounter = Integer.parseInt(tuple.getField(13).getValue().toString());
			boolean nogo = (boolean) tuple.getField(14).getValue();
			int nogoCounter = Integer.parseInt(tuple.getField(15).getValue().toString());
			int stepCounter = Integer.parseInt(tuple.getField(16).getValue().toString());
			long messageTime = Long.parseLong(tuple.getField(17).getValue().toString());
			boolean silencetrigger = (boolean) tuple.getField(18).getValue();
			String response = tuple.getField(19).getValue().toString();
			String endingquality = tuple.getField(20).getValue().toString();

			double writingSpeed;
			if (messageTime > 0) {
				writingSpeed = ((double) 1000 * requestSentence.length()) / ((double) messageTime);
			} else {
				writingSpeed = requestSentence.length();
			}
			int writeSpeed = (int) Math.round(writingSpeed);
			score[session] = score[session] + (int) writeSpeed;
			score[session] = (int) (score[session] - requestSentence.length() * 0.1);

			if (geschimpft) {
				score[session] = score[session] - 100;
			}
			if (aggressiv) {
				score[session] = score[session] - 200;
			}
			if (nogo) {
				score[session] = score[session] - 50;
			}
			if (polite) {
				score[session] = score[session] + 100;
			}
			if (silencetrigger) {
				score[session] = score[session] - 100;
			}
			System.out.println("\n Score: " + score[session]);
			Tuple phaseStarter = new Tuple(new Field[] { new Field(playerName), new Field(sentenceOpener),
					new Field(requestText), new Field(session), new Field(answerquality), new Field(ID),
					new Field(emotion), new Field(geschimpft), new Field(swearwordCounter), new Field(aggressiv),
					new Field(aggressionCounter), new Field(theme), new Field(polite), new Field(politenesscounter),
					new Field(nogo), new Field(nogoCounter), new Field(stepCounter), new Field(messageTime),
					new Field(silencetrigger), new Field(response), new Field(score[session]),
					new Field(endingquality) });
			tupleSpace.write(phaseStarter, space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}

	}

}
