package topic.creation.searchEngine;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Agora {

	@SuppressWarnings("unchecked")
	private JSONObject addObject(JSONArray root, String key) {
		JSONObject node = new JSONObject();
		root.add(node);
		JSONObject node2 = new JSONObject();
		node.put(key, node2);
		return node2;
	}
	@SuppressWarnings("unchecked")
	private JSONObject addObject(JSONObject root, String key) {
		JSONObject node = new JSONObject();
		root.put(key, node);
		return node;
	}
	@SuppressWarnings("unchecked")
	private JSONArray addList(JSONObject root, String key) {
		JSONArray node = new JSONArray();
		root.put(key, node);
		return node;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> query(String className, ArrayList<String> methods, int number) throws Exception {
		JSONObject requestHierarchy = new JSONObject();
		JSONArray classShould = addList(addObject(addObject(requestHierarchy, "query"), "bool"), "should");
		requestHierarchy.put("size", number);
		addObject(classShould, "match").put("code.class.name", className);
		
		JSONObject nested;
		JSONArray methodShould;
		
		for(String method : methods) {
			String[] splt = method.split("\\s+");
			String methodName = splt[splt.length-1];
			String methodType = splt.length==1?null:splt[0];
			nested = addObject(classShould, "nested");
			nested.put("path", "code.class.methods");
			methodShould = addList(addObject(addObject(nested, "query"), "bool"), "should");
			addObject(methodShould, "match").put("code.class.methods.name", methodName);
			if(methodType!=null)
				addObject(methodShould, "term").put("code.class.methods.returntype", methodType);
		}
		

		String requestBody = requestHierarchy.toJSONString();
		//Logger.getLogger("topic.creation").info("JSON request "+requestBody);
		StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_FORM_URLENCODED);
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("http://agora.ee.auth.gr:8080/_search");
		request.setEntity(entity);
		HttpResponse response = null;
	    response = httpClient.execute(request);
	    //System.out.println("Response status: "+response.getStatusLine().toString());
	    String responseText = EntityUtils.toString(response.getEntity());
	    JSONObject result = (JSONObject) (new JSONParser()).parse(responseText);
	    JSONArray hits = (JSONArray)((JSONObject)result.get("hits")).get("hits");
	    ArrayList<String> foundCode = new ArrayList<String>();
	    for(int i=0;i<hits.size();i++) {
	    	JSONObject source = (JSONObject)((JSONObject)hits.get(i)).get("_source");
			Logger.getLogger("topic.creation").finest("Source "+source.get("fullpathname").toString());
	    	//System.out.println(source.get("fullpathname").toString());
	    	foundCode.add(source.get("content").toString());
	    }
	    return foundCode;
	}
}
