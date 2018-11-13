package topic.summary;

import java.util.HashMap;

public interface Summarizer {
	public String createSummary(HashMap<String, Double> topic) throws Exception;
}
