package topic.creation;

import java.util.HashMap;

import analysis.code.ASTEntity;
import analysis.code.ClassObject;

public interface TopicCreator {
	public HashMap<String, Double> createMethodTopic(ClassObject classObject, ASTEntity method) throws Exception;
}
