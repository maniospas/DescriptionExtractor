package topic.summary;

import java.util.HashMap;
import auth.eng.textManager.WordModel;

public class PhraseSummarizer implements Summarizer {
	public String createSummary(HashMap<String, Double> topic) {
		WordModel singleWordModel = new WordModel.BagOfUnstemmedWords();
		HashMap<String, Double> TFpredicate = new HashMap<String, Double>();
		for(String comments : topic.keySet())
			for(String sentence : WordModel.getTextSentences(comments))
				for(String word : singleWordModel.getSentenceFeatures(sentence)) 
					if(word!=null)
						TFpredicate.put(word,TFpredicate.getOrDefault(word, 0.) + topic.get(comments));
		WordModel wordModel = new WordModel.PhraseTrigram();
		HashMap<String, Double> TF = new HashMap<String, Double>();

		for(String comments : topic.keySet())
			if(topic.get(comments)>0)
				for(String sentence : WordModel.getTextSentences(comments))
					for(String word : wordModel.getSentenceFeatures(sentence)) 
						if(word!=null) 
							TF.put(word,TF.getOrDefault(word, 0.) + topic.get(comments));
		String mixgram = "";
		double bestValue = 0;
		for(String gram : TF.keySet()) {
			//System.out.println(gram +" : "+TF.get(gram));
			if(bestValue<TF.get(gram) && gram.contains(" ")) {
				bestValue = TF.get(gram);
				mixgram = gram;
			}
		}
		TF.put(mixgram, 0.);
		while(bestValue>0) {
			String prevmixgram = mixgram;
			bestValue = 0;
			String[] words = prevmixgram.split("\\s");
			String selected = "";
			for(String gram : TF.keySet())
				if(bestValue<TF.get(gram) && words[words.length-1].equals(gram.split("\\s")[0])) {
					bestValue = TF.get(gram);
					mixgram = prevmixgram+gram.substring(words[words.length-1].length());
					selected = gram;
				}
			for(String gram : TF.keySet())
				if(bestValue<TF.get(gram) && words[0].equals(gram.split("\\s")[gram.split("\\s").length-1])) {
					bestValue = TF.get(gram);
					mixgram = gram+prevmixgram.substring(words[0].length());
					selected = gram;
				}
			/*if(bestValue<=initValue*0.2) {
				mixgram = prevmixgram;
				break;
			}*/
			TF.put(selected, 0.);
		}
		/*for(String pred : mixgram.split("\\s"))
			if(TFpredicate.get(pred)<initValue && !wordModel.isStopword(pred))
				mixgram = (" "+mixgram+" ").replace(" "+pred+" ", " ").trim();*/
		return mixgram;
	}
}
