package analysis.similarity;

import analysis.code.ASTEntity;
import auth.eng.textManager.WordModel;
import auth.eng.textManager.stemmers.PorterStemmer;

public class NameAndTypeSimilarity implements ASTSimilarity {
	protected WordModel wordModel = new WordModel.BagOfWords(new PorterStemmer());
	protected double cosineSimilarity(String[] list1, String[] list2) {
		if(list1.length==0 && list2.length==0)
			return 1;
		if(list1.length==0 || list2.length==0)
			return 0;
		int i = 0;
		for(String str1 : list1)
			for(String str2 : list2)
				if(str1.equals(str2))
					i += 1;
		return ((double)i)/Math.sqrt(list1.length*list2.length);
	}
	@Override
	public double similarity(ASTEntity node1, ASTEntity node2) {
		if(!node1.isComparable(node2))
			return Double.NEGATIVE_INFINITY;
		return 0.5*cosineSimilarity(wordModel.getSentenceFeatures(node1.getName()), wordModel.getSentenceFeatures(node2.getName()))
			  +0.5*cosineSimilarity(wordModel.getSentenceFeatures(node1.getType()), wordModel.getSentenceFeatures(node2.getType()));
	}

}
