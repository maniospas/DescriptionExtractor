package analysis.code;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

/**
 * <h1>ClassObject</h1>
 * This represents a single java class implementation.
 * To save storage space, AST structures are not serialized.
 * @author Manios Krasanakis
 */
public class ClassObject implements Serializable {
	private static final long serialVersionUID = -3542367683678872521L;
	private String contribution;
	private String content;
	private transient ASTEntity rootEntity;
	private transient ASTEntityBuilder factory;
	
	/**
	 * Constructor that loads the class from a specified file.
	 * @param path the file path
	 * @throws Exception
	 */
	public ClassObject(String path) throws Exception {
		StringBuilder contentBuilder = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    String line = br.readLine();
		    while (line != null) {
		    	contentBuilder.append(line);
		    	contentBuilder.append(System.lineSeparator());
		        line = br.readLine();
		    }
		}
		
		contribution = (new File(path)).getName();
		content = contentBuilder.toString();
	}
	/**
	 * Constructor that creates a custom class with specified contribution source and content.
	 * @param contribution
	 * @param content
	 */
	public ClassObject(String contribution, String content) {
		this.contribution = contribution;
		this.content = content;
	}
	/**
	 * <h1>copy</h1>
	 * @return a copy of this class
	 */
	public ClassObject copy() {
		return new ClassObject(contribution, content);
	}
	/**
	 * <h1>getContribution</h1>
	 * @return the contributing source
	 */
	public String getContribution() {
		return contribution;
	}
	/**
	 * <h1>getContent</h1>
	 * @return the raw textual content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * <h1>toString</h1>
	 * @return the name of the root entity
	 */
	@Override
	public String toString() {
		try {
			ASTEntity root = getRoot();
			return root.getName();
		}
		catch(Exception e) {
			return "";
		}
	}
	/**
	 * <h1>getRoot</h1>
	 * Uses {@link ASTEntityBuilder} to generate AST structure.
	 * @return the root {@link ASTEntity}
	 */
	public ASTEntity getRoot() throws Exception {
		if(factory==null) 
			factory = createASTBuilderFactory();
		if(rootEntity==null) 
			rootEntity = factory.extractStructure();
		return rootEntity;
	}
	/**
	 * <h1>setContent</h1>
	 * @param content the new content
	 */
	public void setContent(String content) {
		this.content = content;
		factory = null;
		rootEntity = null;
	}
	/**
	 * <h1>createASTBuilderFactory</h1>
	 * @return an ASTBuilderFactory, which can be used to modify source code or directly obtain low-level components of source syntax
	 */
	public ASTEntityBuilder createASTBuilderFactory() {
		return new ASTEntityBuilder(this);
	}
	
	public ASTEntity searchForSimilar(ASTEntity entity) throws Exception {
		for(Node child : getRoot().collapse()) {
			if(child.getName().equals(entity.getName()) && child.getLevel()==entity.getLevel())
				return (ASTEntity)child;
		}
		throw new RuntimeException("Could not find similar entity");
		//return null;
	}
	public void updateContentComments() {
		if(rootEntity==null)
			return;
		for(Node entity : rootEntity.collapse())
			if(entity instanceof ASTEntity) {
				try {
					if(!entity.getComments().toString().isEmpty())
						factory.appendComments((ASTEntity)entity, entity.getComments());
				}
				catch(Exception e) {
					System.err.println(e.toString());
				}
			}
		factory.applyCommentAppends();
	}
}
