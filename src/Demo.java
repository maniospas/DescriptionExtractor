import java.util.logging.Logger;

import analysis.code.ASTEntity;
import analysis.code.ClassObject;
import analysis.code.Node;
import topic.creation.AgoraTopicCreator;

public class Demo {
	
	public static void main(String[] args) throws Exception {
		ClassObject classObject = new ClassObject("tests/Test.java");
		
		for(Node child : classObject.getRoot().getChildren()) {
			if(child.getName().equals(classObject.getRoot().getName()))
				continue;
			try {
				ASTEntity method = (ASTEntity)child;
				String comments = (new topic.summary.SentenceSummarizer()).createSummary((new AgoraTopicCreator(500)).createMethodTopic(classObject, method));
				method.updateComments(comments);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		classObject.updateContentComments();
		
		System.out.println(classObject.getContent());
	}

}
