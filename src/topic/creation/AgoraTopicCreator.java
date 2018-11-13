package topic.creation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import analysis.code.ASTEntity;
import analysis.code.ClassObject;
import analysis.code.Node;
import analysis.similarity.BUMSCI;
import topic.creation.searchEngine.Agora;

public class AgoraTopicCreator implements TopicCreator {
	private int size;
	public AgoraTopicCreator() {
		this(100);
	}
	public AgoraTopicCreator(int size) {
		this.size = size;
	}
	@Override
	public HashMap<String, Double> createMethodTopic(ClassObject classObject, ASTEntity method) throws Exception {
		if(!method.isMethod() || method.getParent()==null)
			throw new RuntimeException("Provided entity must be a method of a class");
		//get and organize results
		Agora agora = new Agora();
		ArrayList<String> methods = new ArrayList<String>();
		methods.add(method.getName());
		String className = classObject.getRoot().getName();
		ArrayList<ClassObject> foundClasses = new ArrayList<ClassObject>();
		for(String code : agora.query(className, methods, size)) {
			try{
				ClassObject obj = new ClassObject("AGORA", code);
				obj.getRoot();//try to find a root to throw structural exceptions
				foundClasses.add(obj);
			}
			catch(Exception e) {
			}
		}
		Logger.getLogger("topic.creation").info("Found "+foundClasses.size()+" AGORA classes");
		//constuct topic
		HashMap<String, Double> topic = new HashMap<String, Double>();
		for(ClassObject obj : foundClasses) 
			for(Node node : obj.getRoot().collapse()) 
				if(node.getComments().length()!=0) {
					double similarity = (new BUMSCI()).similarity((ASTEntity)node, method);
					topic.put(node.getComments(), similarity);
					//if(method.getName().contains("train") && similarity>0) 
						//System.out.println(method.getStackTrace()+" "+node.getStackTrace()+" : "+similarity)
				}
		return topic;
	}
	
}
