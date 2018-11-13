package topic.creation.searchEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GitHub {
	private Runtime runtime;
	public GitHub(String username, String password) {
		runtime = Runtime.getRuntime();
		runCommand("curl -i https://api.github.com -u"+username+":"+password);
	}
	
	public ArrayList<String> searchRepos(String query) {
		ArrayList<String> repos = new ArrayList<String> ();
		for(String line : runCommand("curl https://api.github.com/search/repositories?q="+query+"+in:file+language:java")) {
			line = line.trim();
			if(line.startsWith("\"full_name\":")) {
				String repo = line.substring(14, line.length()-2);
				repos.add(repo);
				//System.out.println(repo);
			}
		}
		return repos;
	}
	public ArrayList<String> searchRepo(String repo, String query) {
		ArrayList<String> files = new ArrayList<String>();
		//System.out.println("curl https://api.github.com/search/code?q="+query+"+in:file+language:java+repo:"+repo);
		for(String line : runCommand("curl https://api.github.com/search/code?q="+query+"+in:file+language:java+repo:"+repo)) {
			line = line.trim();
			if(line.startsWith("\"html_url\":")) {
				String file = line.substring(13, line.length()-2);
				if(file.endsWith(".java")) {
					file = file.replace("https://github.com/", "https://raw.githubusercontent.com/").replace("/blob/", "/");
					//System.out.println(file);
					files.add(file);
				}
			}
		}
		return files;
	}
	public void downloadFile(String downloadLink, String targetFolder, boolean replace) {
		String simpleName = downloadLink.substring(downloadLink.lastIndexOf('/')+1);
		String fileName = targetFolder+simpleName;
		int i = 1;
		while(!replace && (new File(fileName)).exists()) {
			i++;
			fileName = targetFolder+simpleName.substring(0, simpleName.lastIndexOf('.'))+i+simpleName.substring(simpleName.lastIndexOf('.'));
		}
		runCommand("curl "+downloadLink+" --output "+fileName);
	}
	
	protected ArrayList<String> runCommand(String command) {
		try {
			Process proc = runtime.exec(command);
			InputStream stdin = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			ArrayList<String> lines = new ArrayList<String>();
			while ( (line = br.readLine()) != null) {
				lines.add(line);
			    System.out.println(line);
			}
			return lines;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
}
