package topic.summary;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import auth.eng.textManager.WordModel;
import auth.eng.textManager.stemmers.NoStemmer;
import auth.eng.textManager.stemmers.PorterStemmer;

public class SentenceSummarizer implements Summarizer {
	@Override
	public String createSummary(HashMap<String, Double> topic) throws Exception {
		WordModel multigram =  new WordModel.MultiGram(5, new PorterStemmer());
		ArrayList<Entry<double[], Double>> data = new ArrayList<Entry<double[], Double>>();
		
		for(Entry<String, Double> entry : topic.entrySet()) 
			for(String sentence : WordModel.getTextSentences(entry.getKey())) 
				data.add(new AbstractMap.SimpleEntry<double[], Double>(multigram.getSentenceFeatureVector(sentence), entry.getValue()));

		double[] sum = new double[multigram.getCurrentFeatureVectorLength()];
		double[] weightedSum = new double[multigram.getCurrentFeatureVectorLength()];
		int compatible = 0;
		for(Entry<double[], Double> entry : data) {
			double[] key = entry.getKey();
			double value = entry.getValue();
			for(int i=0;i<key.length;i++) {
				sum[i] += key[i];
				if(value>0)
					weightedSum[i] += key[i]*value;
			}
			if(value>0)
				compatible++;
		}
			
		Logger.getLogger("topic.summary").info("Created topic with "+sum.length+" features in "+data.size()+" entities out of which "+compatible+" were relevant");
		
		if(compatible<10)
			Logger.getLogger("topic.summary").warning("Too few candidates ("+compatible+") to select topic description");
		
		double bestValue = 0;
		String bestText = "";
		for(String comments : topic.keySet())
			if(topic.get(comments)>0) {
				for(String sentence : WordModel.getTextSentences(comments)) {
					if(sentence.length()==0 || !Character.isLetter(sentence.charAt(0)) || sentence.contains("="))
						continue;
					double[] vector = multigram.getSentenceFeatureVector(sentence);
					double vectorSimilarity = 0;
					for(int i=0;i<weightedSum.length;i++)
						vectorSimilarity += vector[i]*weightedSum[i]/Math.log(1+sum[i]);
					//vectorSimilarity *= topic.get(comments);
					int len = 0;
					for(int i=0;i<vector.length;i++)
						len += vector[i];
					vectorSimilarity /= len;
					//value *= Math.exp(-id);
					if(vectorSimilarity>bestValue && len>2) {
						bestValue = vectorSimilarity;
						bestText = sentence;
					}
				}
		}
		return bestText;
	}

}
