package topic.creation;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import analysis.code.ASTEntity;
import analysis.code.ClassObject;
import analysis.code.Node;
import analysis.similarity.BUMSCI;
import topic.creation.searchEngine.Agora;

public class DiskTopicCreator implements TopicCreator {
	private static void deleteFolderContents(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	deleteFolderContents(f);
	            	f.delete();
	            } else {
	                f.delete();
	            }
	        }
	    }
	    //folder.delete();
	}
	private static ArrayList<ClassObject> load(File file) {
		ArrayList<ClassObject> classObjects = new ArrayList<ClassObject>();
		if(file.isFile() && file.getPath().endsWith(".java"))
			try {
				ClassObject classObject = new ClassObject(file.getPath());
				System.out.println(file.getPath()+": "+classObject.getRoot().getChildren().size()+" methods");
				classObjects.add(classObject);
			}
			catch(Exception e) { 
				//System.err.println(e.toString());
			}
		else if(file.isDirectory()) 
		    for (File subfile : file.listFiles()) 
		    	classObjects.addAll(load(subfile));
		return classObjects;
	}
	
	public HashMap<String, Double> createMethodTopic(ClassObject classObject, ASTEntity method) throws Exception {
		if(!method.isMethod() || method.getParent()==null)
			throw new RuntimeException("Provided entity must be a method of a class");
		/*
		System.out.println(method.getStackTrace());
		System.out.println("Asserting that self-similarity is 1 : "+(new BUMSCI()).similarity(method, method));
		*/
		
		Agora agora = new Agora();
		ArrayList<String> methods = new ArrayList<String>();
		methods.add(method.getName());
		String className = classObject.getRoot().getName();
		int i = 0;
		deleteFolderContents(new File("data/"));
		for(String code : agora.query(className, methods, 100)) 
			try (PrintWriter out = new PrintWriter("data/"+className+i+".java")) {
			    out.println(code);
			    i++;
			    out.close();
			}
		ArrayList<ClassObject> foundClasses = load(new File("data/"));
		HashMap<String, Double> topic = new HashMap<String, Double>();
		for(ClassObject foundClassObject : foundClasses) 
			for(Node node : foundClassObject.getRoot().collapse()) 
				if(node.getComments().length()!=0) {
					double similarity = (new BUMSCI()).similarity((ASTEntity)node, method);
					topic.put(node.getComments(), similarity);
				}
		return topic;
	}
}
