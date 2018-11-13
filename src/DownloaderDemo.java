import java.io.PrintWriter;
import java.util.ArrayList;

import topic.creation.searchEngine.Agora;

public class DownloaderDemo {

	public static void main(String[] args) throws Exception {
		/*GitHub gitHub = new GitHub("maniospas@hotmail.com", "");
		String query = "left+right";
		for(String repo : gitHub.searchRepos(query)) 
			for(String file : gitHub.searchRepo(repo, query))
				gitHub.downloadFile(file, "data/", false);
		*/
		Agora agora = new Agora();
		ArrayList<String> methods = new ArrayList<String>();
		methods.add("push");
		methods.add("pop");
		String className = "Stack";
		int i = 0;
		for(String code : agora.query(className, methods, 20)) 
			try (PrintWriter out = new PrintWriter("data/"+className+i+".java")) {
			    out.println(code);
			    i++;
			}
	}
	
	

}
