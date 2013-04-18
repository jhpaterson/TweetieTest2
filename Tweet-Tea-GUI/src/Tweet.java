

/**
 * A Tweet
 * @author Geoffrey
 *
 */

public class Tweet
{
	
	
	/**
	 * created at
	 */
	private String created_at;
	
	/**
	 * Entities
	 */
	
	private Entities entities;
	
	/**
	 * id 
	 */
	private String id;
	/**
	 * id_str
	 */
	private String id_str;
	
	/**
	 * tweet's text
	 */
	private String text;
	
	private String source;
	private boolean truncated ;
	private String in_reply_to_status_id;
	private String in_reply_to_status_id_str;
	private String in_reply_to_user_id;
	private String in_reply_to_user_id_str;
	
	private String in_reply_to_screen_name;
	
	/**
	 * tweet's user
	 */
	private User user;
	
	/**
	 * Like geolocation
	 */
	private Place place;
	private Integer retweet_count;
	private Integer favorite_count;
	private boolean favorited;
	private boolean retweeted;
	private boolean possibly_sensitive;
	private String lang;
	
	/**
	 * User screen name
	 */
	private String from_user;
	/**
	 * User ID
	 */
	private String from_user_id;	// we use string because we dont need to compute something on id. it's simpler
	private String from_user_id_str;
	/**
	 * User name
	 */
	private String from_user_name;
	
	
	private String to_user;
	private String to_user_id;
	private String to_user_id_str;
	private String to_user_name;
	
	/**
	 * Profile image (prefer https for everything)
	 */
	private String profile_image_url;
	private String profile_image_url_https;

	
	
	public String toString()
	{
		String to_return= "Posted " + text + " by " ;
		
		if(user != null ){
			to_return += user.getName();
		}
		else{
			to_return+= from_user;
		}
		to_return+=		"Created At :" + created_at ;
		
		if(entities!=null){
			to_return+= entities.toString();
		}
		
		to_return+="      ID : "+id;
		
		return to_return;
				
	}
	
	/**
	 * Getters
	 **/
	public String getID(){
		return id;
	}
	
	public String getMessage(){
		return text;
	}


	public String getDate(){
		return created_at;
	}
	
	public User getUser(){
		return user;
	}
	public String  getFromUser(){
		return from_user;
	}
	public String  getFromUserName(){
		return from_user_name;
	}
	public String getProfileImageURL(){
		return profile_image_url_https;
	}
	
}
