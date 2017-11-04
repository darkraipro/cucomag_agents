package aimlbotAgent;

import java.util.ArrayList;
import java.util.List;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class AimlbotCallback implements Callback {

	TupleSpace tupleSpace;
	int sessionNumber;
	List<Bot> botList;
	List<Chat> chatList;
	static final String space = "COLCOMA";

	public AimlbotCallback(TupleSpace tupleSpace, int sessionNumber) {
		this.tupleSpace = tupleSpace;
		this.sessionNumber = sessionNumber;
		this.botList = new ArrayList<>();
		this.chatList = new ArrayList<>();
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		Tuple tuple = afterCmd;

		String playerName = tuple.getField(0).getValue().toString();

		String sentenceOpener = tuple.getField(1).getValue().toString();
		String request = tuple.getField(2).getValue().toString();
		String wholeSentence = sentenceOpener + request;
		int session = Integer.parseInt(tuple.getField(3).getValue().toString());
		int answerquality = Integer.parseInt(tuple.getField(4).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		int emotion = Integer.parseInt(tuple.getField(6).getValue().toString());
		boolean rudeness = (boolean) tuple.getField(7).getValue();
		int rudenessCounter = Integer.parseInt(tuple.getField(8).getValue().toString());
		boolean aggression = (boolean) tuple.getField(9).getValue();
		int aggressionCounter = Integer.parseInt(tuple.getField(10).getValue().toString());
		String theme = tuple.getField(11).getValue().toString();
		boolean politeness = (boolean) tuple.getField(12).getValue();
		int politenessCounter = Integer.parseInt(tuple.getField(13).getValue().toString());
		boolean nogo = (boolean) tuple.getField(14).getValue();
		int nogoCounter = Integer.parseInt(tuple.getField(15).getValue().toString());
		int stepCounter = Integer.parseInt(tuple.getField(16).getValue().toString());
		long messageTime = Long.parseLong(tuple.getField(17).getValue().toString());
		boolean silenceBool = (boolean) tuple.getField(18).getValue();
		String endingquality = tuple.getField(19).getValue().toString();

		if (session > botList.size()) {
			String botname = determineBotAndScenarioName(theme);
			if (botname.equals("ILLEGAL THEME")) {
				throwException();
			}
			String path = determineOSTypeForDirectory();
			botList.add(new Bot(botname, path));
			chatList.add(new Chat(botList.get(session - 1)));
		}
		String botResponse;
		if (rudeness) {
			botResponse = chatList.get(session - 1).multisentenceRespond("RUDENESS");
		} else if (aggression) {
			botResponse = chatList.get(session - 1).multisentenceRespond("AGGRESSION");
		} else {
			botResponse = chatList.get(session - 1).multisentenceRespond(wholeSentence);
		}

		// Simulate typing time of bot
		long sentenceLength = (long) botResponse.length();
		long waitTime = 500 + sentenceLength * 70;
		synchronized (AimlbotCallback.this) {
			try {
				this.wait(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Tuple tmp = new Tuple(new Field[] { new Field(playerName), new Field(sentenceOpener), new Field(request),
				new Field(session), new Field(answerquality), new Field(ID), new Field(emotion), new Field(rudeness),
				new Field(rudenessCounter), new Field(aggression), new Field(aggressionCounter), new Field(theme),
				new Field(botResponse), });

		try {
			tupleSpace.write(tmp, space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		Tuple tmp2 = new Tuple(new Field[] { new Field(playerName), new Field(sentenceOpener), new Field(request),
				new Field(session), new Field(answerquality), new Field(ID), new Field(emotion), new Field(rudeness),
				new Field(rudenessCounter), new Field(aggression), new Field(aggressionCounter), new Field(theme),
				new Field(politeness), new Field(politenessCounter), new Field(nogo), new Field(nogoCounter),
				new Field(stepCounter), new Field(messageTime), new Field(silenceBool), new Field(botResponse),
				new Field(endingquality),});
		try {
			tupleSpace.write(tmp2, space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}

	}

	private String determineBotAndScenarioName(String theme) {
		switch (Integer.parseInt(theme)) {
		case 1:
			return "001";
		case 2:
			return "002";
		case 3:
			return "003";
		default:
			return "ILLEGAL THEME";
		}
	}

	private void throwException() {
		try {
			throw new Exception("Illegal theme. Can not happen");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String determineOSTypeForDirectory() {
		final String OS_TYPE = System.getProperty("os.name");
		if (OS_TYPE.contains("win")) {
			return "src\\main\\java\\ressources\\";
		} else {
			return "src/main/java/ressources/";
		}
	}

}
