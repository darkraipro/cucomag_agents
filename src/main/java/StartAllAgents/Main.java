package StartAllAgents;

import aggressionAgent.AggressionAnalysis;
import aimlbotAgent.AimlbotAnalysis;
import collectingAgent.CollectingAnalysis;
import info.collide.sqlspaces.commons.TupleSpaceException;
import nogoAgent.NogoAnalysis;
import politenessAgent.PolitenessAnalysis;
import registerAgent.RegisterAnalysis;
import rudenessAgent.RudenessAnalysis;
import scoreAgent.ScoreAnalysis;
import serializationAgent.SerializationAnalysis;
import silenceAgent.SilenceAnalysis;
import stepAgent.StepsAnalysis;

public class Main {

	static PolitenessAnalysis politeAgent;
	static StepsAnalysis stepAgent;
	static NogoAnalysis nogoAgent;
	static RudenessAnalysis rudenessAgent;
	static RegisterAnalysis registerAgent;
	static SilenceAnalysis silenceAgent;
	static AggressionAnalysis aggressionAgent;
	static ScoreAnalysis scoreAgent;
	static CollectingAnalysis collectingAgent;
	static SerializationAnalysis serializationAgent;
	static AimlbotAnalysis aimlbotAgent;
	
	public static void main(String[] args) throws NumberFormatException, TupleSpaceException {
		politeAgent = new PolitenessAnalysis();	
		stepAgent = new StepsAnalysis();
		nogoAgent = new NogoAnalysis();	
		rudenessAgent = new RudenessAnalysis();
		registerAgent = new RegisterAnalysis();
		silenceAgent = new SilenceAnalysis();
		aggressionAgent = new AggressionAnalysis();
		scoreAgent = new ScoreAnalysis();
		collectingAgent = new CollectingAnalysis();
		serializationAgent = new SerializationAnalysis();
		aimlbotAgent = new AimlbotAnalysis();
		while(true){
			
		}
	}

}
