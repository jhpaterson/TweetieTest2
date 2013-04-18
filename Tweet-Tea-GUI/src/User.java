/**
 * A user
 * @author Geoffrey
 *
 */
public class User
{
	/**
	 * User's name
	 */
	private String name;
	/**
	 * User's description
	 */
	private String description;
	
	private String id;
	private String id_str;
	private String screen_name;
	private String location;
	private String url;
	
	private boolean is_protected;
	
	private Integer followers_count;
	private Integer friends_count;
	private Integer listed_count;
	private String created_at;
	private Integer favourites_count;
	
	private boolean geo_enabled;
	
	
	
	private String profile_image_url;
	private String profile_image_url_https;
	
	
	public String  getName(){
		return name;
	}
	/**
	 * String which represents an user
	 */
	public String toString()
	{
		return "Name: " + name +
		" Description: " + description;
	}
	
	public String getScreenName(){
		return screen_name;
	}


	/**
	 * Getters
	 */
	
	
	public String getUrl(){
		return url;
	}
	
	public String getProfileImageURL(){
		return profile_image_url_https;
	}


}
