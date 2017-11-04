package nogoAgent;

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

public class NogoCallback implements Callback{
	TupleSpace tupleSpace;
	List<String> nogoWordList;
	public static final String space = "COLCOMA";
	int nogoCounter;
	
	public NogoCallback(TupleSpace tupleSpace){
		this.tupleSpace = tupleSpace;
		loadList();
	}
	
	private void loadList() {
		nogoWordList = new ArrayList<>();
		try(Scanner scanner = new Scanner(new File("src/main/java/ressources/nogoList.txt"))){
			while(scanner.hasNextLine()){
				nogoWordList.add(scanner.nextLine().toLowerCase().trim());
			}
			System.out.println("Reading nogo List complete.");
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		Tuple tuple = afterCmd;
		String name = tuple.getField(0).getValue().toString();
		String sentenceOpener = tuple.getField(1).toString().toLowerCase();
		String sentenceTippedByUser = tuple.getField(2).toString().toLowerCase();
		String wholeStatement = (sentenceOpener + sentenceTippedByUser).trim();
		int sessionID = Integer.parseInt(tuple.getField(3).getValue().toString());
		int ID = Integer.parseInt(tuple.getField(5).getValue().toString());
		long tupleID = tuple.getTupleID().getID();
		boolean nogo = false;
		
		if(!"".equals(name)){
			Tuple takeTupleIfMatches = new Tuple(new Field[] { new Field(sessionID), new Field("NoGo-Feedback"),
					new Field(name), new Field(nogoCounter) });
			Tuple counterTuple;
			try {
				counterTuple = tupleSpace.take(takeTupleIfMatches);
				if (counterTuple != null) {
					nogoCounter = Integer.parseInt(counterTuple.getField(3).getValue().toString());
				}else{
					nogoCounter = 0;
				}
			} catch (TupleSpaceException e) {
				e.printStackTrace();
			}
			for(String s : nogoWordList){
				if (wholeStatement.contains(s)) {
					nogo = true;
					nogoCounter++;
					try {
						tupleSpace.write(new Tuple(new Field[] { new Field(sessionID), new Field("NoGo-Feedback"),
								new Field(name), new Field(nogoCounter) }), space);
						break;
					} catch (TupleSpaceException e) {
						e.printStackTrace();
						break;
					}
				}
			}
			if(!nogo){
				try{
					tupleSpace.write(new Tuple(new Field[]{ new Field(sessionID), new Field("NoGo-Feedback"),
							new Field(name), new Field(nogoCounter )
					}), space);
				}catch(TupleSpaceException e){
					e.printStackTrace();
				}
			}
		}
		Tuple tmp = new Tuple(new Field[] { new Field(sessionID), new Field(ID), new Field("NoGo analysis"),
				new Field(nogo), new Field(nogoCounter), new Field(tupleID) });
		try {
			tupleSpace.write(tmp, space);
			System.out.println("\nNoGo: " + tmp.getField(3) + "\nnoGoCounter: " + tmp.getField(4));
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}
		System.out.println("NoGo-call finished.");
		
	}

}
