package serializationAgent;

public class Message {

	private int MessageID;
	private String PlayerName;
	private String SentenceOpener;
	private String PlayerInput;
	private String BotResponse;
	private boolean Aggression;
    private boolean Politeness;
    private boolean Rudeness;
    private boolean NoGoSentence;
    private long MessageTime;
    private int BotDelay;
    private int Answerquality;
    private boolean SilenceTrigger;
    private int ScoreOverall;
    
    public Message(int messageID, String playerName, String sentenceOpener, 
    		String playerInput, String botResponse, boolean aggression,
    		boolean politeness, boolean rudeness, boolean noGoSentence,
    		long messageTime, int botDelay, int answerquality, boolean silenceTrigger,
    		int scoreOverall
    		){
    	this.setMessageID(messageID);
    	this.setPlayerName(playerName);
    	this.setSentenceOpener(sentenceOpener);
    	this.setPlayerInput(playerInput);
    	this.setBotResponse(botResponse);
    	this.setAggression(aggression);
    	this.setPoliteness(politeness);
    	this.setRudeness(rudeness);
    	this.setNoGoSentence(noGoSentence);
    	this.setMessageTime(messageTime);
    	this.setBotDelay(botDelay);
    	this.setAnswerquality(answerquality);
    	this.setSilenceTrigger(silenceTrigger);
    	this.setScoreOverall(scoreOverall);
    }

	public long getMessageTime() {
		return MessageTime;
	}

	public void setMessageTime(long messageTime) {
		MessageTime = messageTime;
	}

	public int getMessageID() {
		return MessageID;
	}

	public void setMessageID(int messageID) {
		MessageID = messageID;
	}

	public String getPlayerName() {
		return PlayerName;
	}

	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}

	public String getSentenceOpener() {
		return SentenceOpener;
	}

	public void setSentenceOpener(String sentenceOpener) {
		SentenceOpener = sentenceOpener;
	}

	public String getPlayerInput() {
		return PlayerInput;
	}

	public void setPlayerInput(String playerInput) {
		PlayerInput = playerInput;
	}

	public String getBotResponse() {
		return BotResponse;
	}

	public void setBotResponse(String botResponse) {
		BotResponse = botResponse;
	}

	public boolean isAggression() {
		return Aggression;
	}

	public void setAggression(boolean aggression) {
		Aggression = aggression;
	}

	public boolean isPoliteness() {
		return Politeness;
	}

	public void setPoliteness(boolean politeness) {
		Politeness = politeness;
	}

	public boolean isNoGoSentence() {
		return NoGoSentence;
	}

	public void setNoGoSentence(boolean noGoSentence) {
		NoGoSentence = noGoSentence;
	}

	public int getBotDelay() {
		return BotDelay;
	}

	public void setBotDelay(int botDelay) {
		BotDelay = botDelay;
	}

	public int getAnswerquality() {
		return Answerquality;
	}

	public void setAnswerquality(int answerquality) {
		Answerquality = answerquality;
	}

	public boolean isSilenceTrigger() {
		return SilenceTrigger;
	}

	public void setSilenceTrigger(boolean silenceTrigger) {
		SilenceTrigger = silenceTrigger;
	}

	public int getScoreOverall() {
		return ScoreOverall;
	}

	public void setScoreOverall(int scoreOverall) {
		ScoreOverall = scoreOverall;
	}

	public boolean isRudeness() {
		return Rudeness;
	}

	public void setRudeness(boolean rudeness) {
		Rudeness = rudeness;
	}
}
