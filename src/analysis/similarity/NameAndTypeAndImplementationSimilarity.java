package analysis.similarity;

import analysis.code.ASTEntity;

public class NameAndTypeAndImplementationSimilarity extends NameAndTypeSimilarity {

	@Override
	public double similarity(ASTEntity node1, ASTEntity node2) {
		if(node1.isMethod() && node2.isMethod() && node1.getImplementation().length()!=0 && node2.getImplementation().length()!=0)
			return 1*super.similarity(node1, node2);// + 0.333*cosineSimilarity(wordModel.getSentenceFeatures(node1.getImplementation()), wordModel.getSentenceFeatures(node2.getImplementation()));
		else
			return super.similarity(node1, node2);
	}
}
