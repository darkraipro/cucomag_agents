package registerAgent;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Field;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;

public class RegisterCallback implements Callback {

	int session;
	String userName1;
	int troublecounter;
	int complaincounter;
	boolean troublemaker;
	TupleSpace tupleSpace;
	public static final String space = "COLCOMA";

	public RegisterCallback(TupleSpace tupleSpace, int session) {
		this.tupleSpace = tupleSpace;
		this.session = session;
		userName1 = "";
		troublemaker = false;
		troublecounter = 0;
		complaincounter = 0;
	}

	@Override
	public void call(Command cmd, int segnum, Tuple afterCmd, Tuple beforeCmd) {
		try {
			Tuple tuple = tupleSpace.take(afterCmd);

			userName1 = tuple.getField(0).getValue().toString();
			String theme = tuple.getField(2).getValue().toString();
			System.out.println("Player name: " + userName1);

			Tuple user1Roles = tupleSpace.take(new Tuple(new Field[] { new Field(userName1), new Field(Integer.class),
					new Field(Integer.class), new Field(String.class) }));
			if (user1Roles != null) {
				if (userName1.equals(user1Roles.getField(0).getValue().toString())) {
					troublecounter = Integer.parseInt(user1Roles.getField(1).getValue().toString());
					complaincounter = Integer.parseInt(user1Roles.getField(2).getValue().toString());
				}
			}
			if (troublecounter > complaincounter) {
				troublemaker = false;
				complaincounter++;
			} else {
				troublemaker = true;
				troublecounter++;
			}
			/*
			 * OUTPUT: (Name|Session|troublemaker) (String|Int|bool)
			 */
			Tuple tmp = new Tuple(new Field[] { new Field(userName1), new Field(session), new Field(troublemaker) });
			tupleSpace.write(tmp, space);

			/*
			 * OUTPUT: (Name|troublecounter|complaincounter) (String| Int | Int)
			 */
			tmp = new Tuple(
					new Field[] { new Field(userName1), new Field(troublecounter), new Field(complaincounter) });

			tupleSpace.write(tmp, space);
			
			tmp = new Tuple(new Field[]{ new Field(session), new Field("Themes analysis"), new Field(theme)});
			tupleSpace.write(tmp, space);
			
			session++;
			userName1 = "";

			/*
			 * Output:O ("SessionNumber"|session) ( String | int)
			 */
			tupleSpace.take(new Tuple(new Field[] { new Field("SessionNumber"), new Field(Integer.class) }));
			tupleSpace.write(new Tuple(new Field[] { new Field("SessionNumber"), new Field(session) }), space);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
		}

	}
}
