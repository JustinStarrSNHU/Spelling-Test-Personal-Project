
public class Word {
	private String word;
	private String wordId;
	private String wordResourceName;
	
	public Word(String wordId, String word, String wordResourceName) {
		this.wordId = wordId;
		this.word = word;
		this.wordResourceName = wordResourceName;
	}
	
	public String getWordId() {
		return wordId;
	}
	
	public String getWord() {
		return word;
	}
	
	public String getWordResourceName() {
		return wordResourceName;
	}
	
	
	public void setWordId(String wordId) {
		this.wordId = wordId;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setWordResourceName(String wordResourceName) {
		this.wordResourceName = wordResourceName;
	}
	
	public String toString() {
		return ("Word [Id =" + wordId + ", Word is: " + word + ", Resource file name: " + wordResourceName + "]");
	}
}
