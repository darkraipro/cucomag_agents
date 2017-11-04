package serializationAgent;

import java.util.ArrayList;
import java.util.List;

public class Root {

	private List<Message> message = new ArrayList<>();
	
	private List<Result> result = new ArrayList<>();

	public List<Message> getMessageList() {
		return message;
	}
	
	public List<Result> getResultList(){
		return result;
	}

}
