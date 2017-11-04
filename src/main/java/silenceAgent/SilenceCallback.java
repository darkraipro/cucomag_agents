package silenceAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class SilenceCallback implements Callback {
	public static final List<Timer> timers = new ArrayList<>();
	private List<Integer> silenceCounters;
	public final String space = "COLCOMA";

	TupleSpace tupleSpace;
	int timerInterval = 120000;

	public SilenceCallback(TupleSpace tupleSpace) throws TupleSpaceException {
		initializeListAndCounters(tupleSpace);
	}

	private void initializeListAndCounters(TupleSpace tupleSpace) {
		this.tupleSpace = tupleSpace;
		silenceCounters = new ArrayList<>();
	}

	/*
	 * (Name | Sentence Opener |Anfrage | Session# | Phase | ID | Emotion |
	 * Geschimpft | Name.Schimpfcounter | Aggro | Name.AggroCounter | Thema )
	 * (String | String | String |int | int | int | int | bool | int | bool |
	 * int | String )
	 */
	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {

		Tuple tuple = afterCmd;
		String playerName = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).getValue().toString();
		int session = Integer.parseInt(tuple.getField(3).getValue().toString());
		int currentID = Integer.parseInt(tuple.getField(5).getValue().toString());
		Runnable runnable = new Runnable() {
			public void run() {
				int countersOfSilencePing = silenceCounters.get(session - 1) + 1;
				silenceCounters.set(session - 1, countersOfSilencePing);
				if (countersOfSilencePing > 4) {
					timers.get(session - 1).cancel();
					timers.get(session - 1).purge();
					System.out.println("Timer closed for good :)");
				}
				if (countersOfSilencePing == 4) {
					System.out.println("4th silence tuple written.");
					System.out.println("name: " + playerName + "\nsession: " + (session));
					Tuple silenceTuple = new Tuple(new Field[] { new Field(playerName), new Field("Ping"),
							new Field("Silence" + countersOfSilencePing), new Field(session),
							new Field(countersOfSilencePing), new Field(currentID), new Field(0), new Field(false),
							new Field(0), new Field(false), new Field(0), new Field(""), new Field(false), new Field(0),
							new Field(false), new Field(0), new Field(0), new Field((long) 90000), new Field(true),
							new Field("unknown") });
					Tuple silenceEndTuple = new Tuple(
							new Field[] { new Field(playerName), new Field(session), new Field("Conversation Ended") });
					try {
						tupleSpace.write(silenceEndTuple, space);
						tupleSpace.write(silenceTuple, space);
					} catch (TupleSpaceException e) {
						e.printStackTrace();
					}
					timers.get(session - 1).cancel();
					timers.get(session - 1).purge();
				}

				if (countersOfSilencePing > 0 && countersOfSilencePing < 4) {
					Tuple silenceTuple = new Tuple(new Field[] { new Field(playerName), new Field("Ping"),
							new Field("Silence" + countersOfSilencePing)
							// For the different bot answer in aiml: Silence1,
							// Silence2,...
							, new Field(session), new Field(countersOfSilencePing),
							// answerquality state = countersOfSilencePing
							new Field(currentID), new Field(0), new Field(false), new Field(0), new Field(false),
							new Field(0), new Field(""), new Field(false), new Field(0), new Field(false), new Field(0),
							new Field(0), new Field((long) 90000), new Field(true), new Field("unknown") });

					try {
						tupleSpace.write(silenceTuple, space);
					} catch (TupleSpaceException e) {
						e.printStackTrace();
					}
					System.out.println("Silence-Tuple written!\n Request: " + silenceTuple.getField(2).getValue()
							+ "\nSilenceCounter: " + countersOfSilencePing + "\nSession: " + session);
				}
			}
		};
		if (session > timers.size()) {
			timers.add(new Timer());
			timers.get(session - 1).schedule(new TimerTask() {
				public void run() {
					runnable.run();
				}
			}, timerInterval);
			silenceCounters.add(0);

		} else {
			timers.get(session - 1).cancel();
			timers.get(session - 1).purge();
			timers.set(session - 1, new Timer());
			timers.get(session - 1).schedule(new TimerTask() {
				public void run() {
					runnable.run();
				}
			}, timerInterval);
			if (!sentenceOpener.contains("Ping") && !sentenceOpener.contains("Player left the conversation")) {
				silenceCounters.set(session - 1, 0);
			}
			if (sentenceOpener.contains("Player left the conversation")) {
				timers.get(session - 1).cancel();
				timers.get(session - 1).purge();
				System.out.println("Timer now closed.");
			}
		}
	}
}
