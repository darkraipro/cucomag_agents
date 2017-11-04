package serializationAgent;

public class Result {

	private String PlayerName;
	private int ResultQuality;
	private int AnswerAmount;
	private long OverallTime;
	
	public Result(String playerName, int resultQuality, int answerAmount, 
			long overallTime){
		this.setPlayerName(playerName);
		this.setResultQuality(resultQuality);
		this.setAnswerAmount(answerAmount);
		this.setOverallTime(overallTime);
	}

	public int getResultQuality() {
		return ResultQuality;
	}

	public void setResultQuality(int resultQuality) {
		this.ResultQuality = resultQuality;
	}

	public int getAnswerAmount() {
		return AnswerAmount;
	}

	public void setAnswerAmount(int answerAmount) {
		this.AnswerAmount = answerAmount;
	}

	public long getOverallTime() {
		return OverallTime;
	}

	public void setOverallTime(long overallTime) {
		this.OverallTime = overallTime;
	}

	public String getPlayerName() {
		return PlayerName;
	}

	public void setPlayerName(String playerName) {
		this.PlayerName = playerName;
	}
}
