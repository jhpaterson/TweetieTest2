
/**
 * Used to parse researches
 * @author Geoffrey
 *
 */
public class Search {

	
	private double completed_in;
	private String max_id;
	private String max_id_str;
	private String next_page;
	private String page;
	private String query;
	private String refresh_url;
	
	private Tweet[] results;
	
	private Integer results_per_page;		// use integer rather than int, it seems better for GSON
	private Integer  since_id;
	private String  since_id_str;
	
	
	public Tweet[] extractTweets(){
		return results;
	}
	

}
