/**
 * Define Resources
 * @author Geoffrey
 *
 */
public class Res {

	
	
	/**
	 * Api's domain
	 */
	public static final String domain="http://api.twitter.com/1/";
	
	public static final String api_key = "ylVhrIQ6Yooukipyz1Qjw";							// comming from your twitter account
	public static final String api_secret = "s8zxt9BNbrynTwccPdkCqUYlngLc36Ta6OPp3RHL3E";
	
	public static final String timeline_prefix = "statuses/user_timeline.json";
	public static final String home_timeline_prefix= "statuses/home_timeline.json";
	public static final String search_prefix= "http://search.twitter.com/search.json";
	public static final String update_prefix = "statuses/update.json";

	/**
	 * Classic Domain
	 */
	
	public static final String twitterPrefix =  "https://twitter.com/";
	
	// Language resources
	
	// TODO : Everything
	
	public static Object lang;
	
	/**
	 * 
	 * @param langCode FR, EN, DE etc...
	 */
	public static void setLang(String langCode){
		
		// No Switch / case here , switch/case on Strings only allowed on java > 1.6
		// default : EN
		if( langCode.equals("FR")){
			Res.lang=new FR();
		}
		else {
			Res.lang=new EN();
		}
		
		
	}
	
	private static class EN{
		public final String title = "TwitterCli";
	}
	
	private static class FR{
		public final String title = "TwitterCli";
	}
}
