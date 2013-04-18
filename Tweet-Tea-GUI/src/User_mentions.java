/**
 * User_mantions from a tweet (meta-info) like "Hi @Tony !"
 * @author Geoffrey
 */
public class User_mentions {

	
	private String screen_name;
	private String name;
	private String id;
	private String id_str;
	private Integer indices[];
	
	
	public String toString() {
		return "< "+ name +" : "+screen_name+" >";
	}
	
	// TODO: Getters

}
