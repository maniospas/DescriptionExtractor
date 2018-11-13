package analysis.similarity;

import analysis.code.ASTEntity;

public interface ASTSimilarity {
	public double similarity(ASTEntity node1, ASTEntity node2);
}
