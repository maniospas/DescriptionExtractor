package analysis.similarity;

import analysis.code.ASTEntity;
import analysis.code.Node;

public class BUMSCI implements ASTSimilarity{
	private NameAndTypeAndImplementationSimilarity baseSimilarity = new NameAndTypeAndImplementationSimilarity();
	@Override
	public double similarity(ASTEntity node1, ASTEntity node2) {
		if(!node1.isComparable(node2))
			return Double.NEGATIVE_INFINITY;
		double ret = iterativeChildSimilarity(node1, node2);
		if(node1.getParent()!=null && node2.getParent()!=null) {
			ret *= iterativeChildSimilarity((ASTEntity)node1.getParent(), (ASTEntity)node2.getParent(), node1, node2, ret);
		}
		return ret;
	}
	private double iterativeChildSimilarity(ASTEntity node1, ASTEntity node2) {
		return iterativeChildSimilarity(node1, node2, null, null, 0);
	}
	private double iterativeChildSimilarity(ASTEntity node1, ASTEntity node2, ASTEntity ignore1, ASTEntity ignore2, double bias) {
		double ret1 = 0;
		double ret2 = 0;
		int count1 = 0;
		int count2 = 0;
		for(Node child1 : node1.getChildren()) if(child1!=ignore1){
			double bestMatch = 0;
			for(Node child2 : node2.getChildren()) if(child2!=ignore2){
				double match = iterativeChildSimilarity((ASTEntity)child1,(ASTEntity)child2);
				if(match>bestMatch) 
					bestMatch = match;
			}
			ret1 += bestMatch;
			count1++;
		}
		for(Node child2 : node2.getChildren()) if(child2!=ignore2){
			double bestMatch = 0;
			for(Node child1 : node1.getChildren()) if(child1!=ignore1){
				double match = iterativeChildSimilarity((ASTEntity)child1,(ASTEntity)child2);
				if(match>bestMatch)
					bestMatch = match;
			}
			ret2 += bestMatch;
			count2++;
		}
		/*if(ignore1!=null && ignore2!=null) {
			ret1 += bias;
			ret2 += bias;
			count1++;
			count2++;
		}*/
		/*double sim = baseSimilarity.similarity(node1, node2);
		ret1 += sim;
		ret2 += sim;
		count1++;
		count2++;
		return Math.sqrt(ret1*ret2/count1/count2);*/
		if(count1!=0 || count2!=0) {
			return Math.sqrt(Math.min(ret1,ret2)/Math.max(count1, count2))*baseSimilarity.similarity(node1, node2);
		}
		else
			return baseSimilarity.similarity(node1, node2);
	}
}
