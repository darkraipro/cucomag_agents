package serializationAgent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class SerializationCallback implements Callback {

	TupleSpace tupleSpace;
	List<List<Message>> sessionArray;
	long[] overallTime;
	static final String space = "COLCOMA";

	public SerializationCallback(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		this.sessionArray = new ArrayList<List<Message>>();
		this.overallTime = new long[30];
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {

		Tuple tuple = afterCmd;

		String name = tuple.getField(0).getValue().toString();

		String sentenceOpener = tuple.getField(1).getValue().toString();
		String request = tuple.getField(2).getValue().toString();
		int session = Integer.parseInt(tuple.getField(3).getValue().toString());
		int gamestate = Integer.parseInt(tuple.getField(4).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		int emotion = Integer.parseInt(tuple.getField(6).getValue().toString());
		boolean rudeness = (boolean) tuple.getField(7).getValue();
		int rudenessCounter = Integer.parseInt(tuple.getField(8).getValue().toString());
		boolean aggression = (boolean) tuple.getField(9).getValue();
		int aggressionCounter = Integer.parseInt(tuple.getField(10).getValue().toString());
		String topic = tuple.getField(11).getValue().toString();
		boolean politeness = (boolean) tuple.getField(12).getValue();
		int politenessCounter = Integer.parseInt(tuple.getField(13).getValue().toString());
		boolean nogo = (boolean) tuple.getField(14).getValue();
		int nogoCounter = Integer.parseInt(tuple.getField(15).getValue().toString());
		int stepCounter = Integer.parseInt(tuple.getField(16).getValue().toString());
		long messageTime = Long.parseLong(tuple.getField(17).getValue().toString());
		boolean silenceBool = (boolean) tuple.getField(18).getValue();
		String botAnswer = tuple.getField(19).getValue().toString();
		int score = Integer.parseInt(tuple.getField(20).getValue().toString());
		
		boolean serialized = false;

		Message m = new Message(ID, name, sentenceOpener, request, botAnswer, aggression, politeness, rudeness, nogo,
				messageTime, 30000, gamestate, silenceBool, score);

		//Session always starts with 1
		if (sessionArray.size() < session) {
			System.out.println("session array initialized");
			sessionArray.add(new ArrayList<Message>());
		}
		sessionArray.get(session - 1).add(m);

		if (gamestate == 6) {
			saveDataAsXML(name, session, ID);
		}

		Tuple tmp = new Tuple(new Field[] { new Field(name), new Field(sentenceOpener), new Field(request),
				new Field(session), new Field(gamestate), new Field(ID), new Field(emotion), new Field(rudeness),
				new Field(rudenessCounter), new Field(aggression), new Field(aggressionCounter), new Field(topic),
				new Field(politeness), new Field(politenessCounter), new Field(nogo), new Field(nogoCounter),
				new Field(stepCounter), new Field(messageTime), new Field(botAnswer), new Field(score),
				new Field(serialized) });
		try {
			tupleSpace.write(tmp, space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}

	}

	private void saveDataAsXML(String name, int session, int ID) {
		XStream xstream = new XStream(new DomDriver());
		xstream.aliasPackage("", "serializationAgent");
		xstream.addImplicitCollection(Root.class, "message");
		xstream.addImplicitCollection(Root.class, "result");
		Root root = new Root();
		for (Message message : sessionArray.get(session - 1)) {
			root.getMessageList().add(message);
			overallTime[session - 1] = overallTime[session - 1] + message.getMessageTime() + 2000;
		}
		Result result = new Result(name, 6, ID, overallTime[session - 1]);
		root.getResultList().add(result);

		// Use at least java 8 update 60 to avoid Windows 10 bug regarding system property
		String directory = determineOSTypeForDirectory();
		File file = new File(directory + name + session + ".xml");

		try (FileWriter fileWriter = new FileWriter(file)) {
			file.createNewFile();
			String data = xstream.toXML(root);
			System.out.println(data);
			fileWriter.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String determineOSTypeForDirectory() {
		String OS_TYPE = System.getProperty("os.name");
		if (OS_TYPE.contains("win")) {
			return "src\\main\\java\\savedata\\";
		} else {
			return "src/main/java/savedata/";
		}
	}

}
