
/**
 * Urls from a tweet ( meta-info)
 * @author Geoffrey
 *
 */
public class Urls {

	private String expanded_url;
	private String url;
	private Integer[] indices;
	private String display_url;
	
	
	public String toString(){
		return "< "+display_url+" >";
	}
	
	/**
	 * getters
	 */
	
	public String getUrl(){
		return url;
	}
	public String getTextUrl(){
		return display_url;
	}

}
