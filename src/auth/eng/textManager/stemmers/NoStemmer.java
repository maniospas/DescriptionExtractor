package auth.eng.textManager.stemmers;

public class NoStemmer implements Stemmer {
	@Override
	public String getName() {
		return "NoStemmer";
	}
	@Override
	public String stem(String word) {
		return word;
	}

}
