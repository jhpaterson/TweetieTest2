import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.*;

import java.awt.Desktop;
import java.net.*;


/**
 * Represents the Twitter API
 * @author Geoffrey
 *
 */
public final class TwitterAPI {

	
	/**
	 * Parser Google Gson
	 */
	private static Gson gson= new Gson();
	
	/**

	 * Used to sign requests.
	 * @see TwitterAPI.getHomeTimeline() || TwitterAPI.auth() for examples
	 */
	private static OAuthService AuthentificationService;
	
	/**
	 * Token gived by tweeter to try to authenticate
	 */
	private static Token requestToken;
	/**
	 * Is the acces key which allow us to sign requests.
	 * @see TwitterAPI.auth()
	 */
	public static Token accessToken;
	
	/**
	 * Saves the access token
	 */
	public static void saveAuthToken(){		
		
		try {
			ObjectOutputStream serializer = null;
			try{
				serializer = new ObjectOutputStream(new FileOutputStream("token.oauth"));
				serializer.writeObject(accessToken);
			}
			finally{
				serializer.close();
			}
			
			
		} catch (FileNotFoundException e) {

			// TODO change this to make something better - GG - FR
			e.printStackTrace();
		} catch (IOException e) {
			// TODO change this to make something better - GG - FR

			e.printStackTrace();
		}		
		
	}
	
	
	/**

	 * Load the access token
	 * @throws Exception : file read may throw Exceptions

	 */
	public static void loadAuthToken() throws Exception {
		
		
		AuthentificationService = new ServiceBuilder()
	      .provider(TwitterApi.class)
	      .apiKey(Res.api_key)
	      .apiSecret(Res.api_secret)
	      .build();
		
		
		try{
			
			ObjectInputStream parser = null;
			try{
				
				parser = new ObjectInputStream(new FileInputStream("token.oauth"));
				accessToken = (Token) parser.readObject();
				
				OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.twitter.com/1/account/verify_credentials.xml");
				AuthentificationService.signRequest(accessToken, request); // the access token from step 4
				Response response = request.send();
				
			}
			finally{
				parser.close();
			}
			
		}catch(FileNotFoundException e){
			throw new Exception("Load auth: file not found");
		}
		catch(IOException e){
			throw new Exception("Load auth: I/O exception");
		}
		catch(ClassCastException e){
			throw new Exception("Load auth: Wrong content or bad file");
		}
		catch(Exception e){
			throw new Exception("Load auth: Unknow error");
		}
	}
	
	
	/**
	 * Give the user's Timeline
	 * @return Array of tweets from the user timeline
	 * @throws Exception timeline unreachable or user not authenticated
	 */
	public static Tweet[] getHomeTimeline() throws Exception{
		

//		if(accessToken==null){
//			//throw new Exception("Need to be authenticated");
//		}

		String url = Res.domain+Res.home_timeline_prefix+"?include_entities=true";
		
		OAuthRequest request = new OAuthRequest(Verb.GET, url);	// we create a request
		
		AuthentificationService.signRequest(accessToken, request);
		Response response = request.send();
		
		Tweet [] parsed;
		try{
			parsed = gson.fromJson( response.getBody() , Tweet[].class);
		}
		catch(Exception e){
			throw new Exception("Unable to get this screen.");
		}
		
		return parsed;
	}
	
	/**

	 * Return a Json representation of a screen (auth not needed)
	 * @param screenName @davidgetta for example;
	 * @throws Exception 
	 */
	public static Tweet[] getScreen(String screenName) throws Exception{
		if(screenName.isEmpty()) return null;
		String url = Res.domain+Res.timeline_prefix+"?screen_name="+screenName+"&include_entities=true";
		System.out.println(url);
		Tweet [] response;
		try{
			response = gson.fromJson(  getJSON(url), Tweet[].class);
		}
		catch(Exception e){
			throw new Exception("Unable to get this screen.");
		}

		return response;
	}
	
	
	/**
	 * Get the tweets from a research (auth not needed)
	 * Authentication not needed
	 * @param query Query : string to search
	 * @return Tweet[]
	 */
	public static Tweet[] search(String query){
		String formated_query= createUrlArgsFromString(query);
		String url = Res.search_prefix+"?q="+formated_query+"&include_entities=false&result_type=mixed";
		  System.out.println(url);
		String json = getJSON(url);
		  System.out.println(json);					// you can look to objects in the console to understand how they are structured
		Search search = parseJSONSearchfromString(json);
		Tweet[] tweets = search.extractTweets();
		
		return tweets;
	}
	
	/**
	 * Send a tweet , auth needed
	 * @param tweet tweet's text
	 */
	public static void send_tweet(String tweet){
		
		String formated_tweet=  createUrlArgsFromString(tweet);
		String url =Res.domain+Res.update_prefix+"?status="+formated_tweet+"&include_entities=true";		
		

		OAuthRequest request = new OAuthRequest(Verb.POST, url);	// A POST request 

		
		AuthentificationService.signRequest(accessToken, request);
		Response response = request.send();
	}
	
	
	
	/**
	 * Respond to a tweet
	 * @param id ID of the tweet
	 */
	public static void respondTweet(String id, String response) throws Exception{
		String url;
		String name;
		OAuthRequest request;
		Response r;
		Tweet t;
		
		//We get the tweet that we want to respond
		id = createUrlArgsFromString(id);
		url = Res.domain+"statuses/show/"+id+".json";
		request = new OAuthRequest(Verb.GET, url);
		AuthentificationService.signRequest(accessToken, request);
		r = request.send();
		
		//We create the tweet from the JSon
		t = gson.fromJson(r.getBody(), Tweet.class);
		
		//Now we get the name of the author
		name = (t.getUser()).getScreenName();
		
		//We create the url to send the response
		response = "@"+name+" "+response;
		response = createUrlArgsFromString(response);
		url = Res.domain+"statuses/update.json?status="+response+"&in_reply_to_status_id="+id;
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We sign the request
		AuthentificationService.signRequest(accessToken, request);
		
		//We send the request
		r = request.send();
		
		//We verify if the tweet was really sent
		try{
			t = gson.fromJson(r.getBody(), Tweet.class);
			
			if(t.getUser() == null){
				//The tweet was'nt sent
				throw new Exception("The tweet was not sent");
			}
		}catch(Exception e){
			throw new Exception("The tweet was not sent");
		}
	}
	
	/**
	 * Destroy a tweet
	 * @id ID of the tweet
	 */
	public static void delete_tweet(String id) throws Exception{
		String url;
		OAuthRequest request;
		Response r;
		Tweet verif; //To verify if the tweet was remove from favorites
		
		//We create the url
		id = createUrlArgsFromString(id);
		url = Res.domain+"statuses/destroy/"+id+".json";
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We sign the request
		AuthentificationService.signRequest(accessToken, request);
		r = request.send();
		
		//We verify if the tweet was deleted
		try{
			verif = gson.fromJson(r.getBody(), Tweet.class);
			
			if(!id.equals(verif.getID()))
				throw new Exception("The tweet was'nt deleted");
		}catch(Exception e){
			throw new Exception("This tweet does'nt exist");
		}
	}
	
	/**
	 * Change user status
	 */
	public static void changeStatus(){
		
		// TODO :  everthing ^^
		
	}
	
	
	/**
	 * Get some of the authenticated user's info
	 * @return User
	 */
	public static User getMyUserInfo(){
		String url =Res.domain+"account/update_profile.json";
		
		OAuthRequest request = new OAuthRequest(Verb.POST, url);	// on cree une requete
		
		AuthentificationService.signRequest(accessToken, request);
		Response response = request.send();
		String json = response.getBody();
		
		User user = gson.fromJson(json, User.class);
		
		return user;
	}
	
	/**
	 * Update the authenticated user description
	 * @return User updated
	 */
	public static User updateDesc(String desc){
		
		String formated_desc = createUrlArgsFromString(desc);
		
		String url =Res.domain+"account/update_profile.json?description=" +formated_desc;
		
		OAuthRequest request = new OAuthRequest(Verb.POST, url);	// on cree une requete
		
		AuthentificationService.signRequest(accessToken, request);
		Response response = request.send();
		String json = response.getBody();
		
		User user = gson.fromJson(json, User.class);
		
		return user;
	}
	
	
	/**
	 * Parse Json from a string to Tweet[]
	 */
	private static Tweet[] parseJSONfromString( String json){
		
		Tweet[] response = gson.fromJson(  json , Tweet[].class);
		return response;
	}
	/**
	 * Parse Json from a string to Search
	 */
	private static Search parseJSONSearchfromString( String json ){
		
		Search response = gson.fromJson(  json , Search.class);
		return response;
	}
	
	
	
	/**
	 * Get Json from Twitter API
	 * @param urlToRead	Url in a correct format 
	 * @return string
	 */
	private static String getJSON(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }
	
	
	/**
	 * Set authentication
	 */

	public static void firstauth() throws Exception {
		
		if(accessToken != null){
			throw new Exception("Already authentified");
		}
		AuthentificationService = new ServiceBuilder()
	      .provider(TwitterApi.class)
	      .apiKey(Res.api_key)
	      .apiSecret(Res.api_secret)
	      .build();

		Token requestToken = AuthentificationService.getRequestToken();
		String authUrl = AuthentificationService.getAuthorizationUrl(requestToken);
		
		
		// Desktop in a class that manage Desktop events
		
		if(Desktop.isDesktopSupported())
		{
			System.out.println("A windows will open, look at your taskbar");
		  try {
			
			Desktop.getDesktop().browse(new URI(authUrl));		// open a webpage in the default browser
		} catch (IOException e) {
			// TODO make something better - GG - FR
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO make something better - GG - FR
			e.printStackTrace();
		}
		}else{
			System.out.println("Go to this url and accept");		// if OS doesn't support Desktop.browse();
			System.out.println(authUrl);
			
		}
		System.out.println("Enter code given by twitter");
		Scanner scanner = new Scanner(System.in);
		String pin = scanner.nextLine();
		Verifier v = new Verifier(pin);
		accessToken = AuthentificationService.getAccessToken(requestToken, v); // the requestToken
		
		System.out.println("(if your curious it looks like this: " + accessToken + " )");

		// we make a request to check the connexion
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.twitter.com/1/account/verify_credentials.xml");
		AuthentificationService.signRequest(accessToken, request); // the access token from step 4
		Response response = request.send();
		
		// everything is ok , we save the token
		saveAuthToken();
	}
	

	/**
	 * Set authentication for the GUI
	 * @return String url
	 */

	public static String getFirstAuthURL() throws Exception {
		
		if(accessToken != null){
			throw new Exception("Already authentified");
		}
		AuthentificationService = new ServiceBuilder()
	      .provider(TwitterApi.SSL.class)
	      .apiKey(Res.api_key)
	      .apiSecret(Res.api_secret)
	      .build();

		requestToken = AuthentificationService.getRequestToken();
		String authUrl = AuthentificationService.getAuthorizationUrl(requestToken);
		
		
		String url = new URI(authUrl).toString();
		
		return url;
		
	}
	/**
	 * Used by the GUI
	 * Get the Code given by twitter and download the token
	 * @param PinCode string 
	 */
	public static void setPinCodefromTwitterAuth(String PinCode){
		
		Verifier v = new Verifier(PinCode);
		System.out.println(PinCode);
		accessToken = AuthentificationService.getAccessToken(requestToken, v); // the requestToken you had from step 2
		
		System.out.println("(if your curious it looks like this: " + accessToken + " )");

		// TODO: Use Res for this String -GG-FR
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1/account/verify_credentials.xml");
		AuthentificationService.signRequest(accessToken, request); // the access token from step 4
		Response response = request.send();
		saveAuthToken();
	}

	
	/**
	 * Encode a argument string in URL format to avoid spaces and special chars
	 * @param s the argument string to encode
	 * @return the encoded argument string which can be concatenated to a domain 
	 */
	private static String createUrlArgsFromString(String s){
		
		// Warning !!!   take care to the order ! 
		//		String retour = 
		//			   s//.replace("%", "%25")
		//				.replace(" ", "%20")
		//				.replace(";", "%3B")
		//				.replace("?", "%3F")
		//				.replace("/", "%2F")
		//				.replace(":", "%3A")
		//				.replace("#", "%23")
		//				.replace("&", "%26")
		//				.replace("=", "%3D")
		//				.replace("+", "%2B")
		//				.replace("$", "%24")
		//				.replace(",", "%2C")
		//				.replace("<", "%3C")
		//				.replace(">", "%3E")
		//				.replace("~", "%7E")
		//				.replace(",", "%2C")
		//				;

		String retour = null;
		
		
		
		try {
			retour= URLEncoder.encode(s,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO make something better - GG - FR
			e.printStackTrace();
		}
		
		return retour;
	}
	
	
	
	/**
	 * @param mess Message to send
	 * @param target User who will receive the message
	 */
	public static void sendMessage(String mess, String target){
		OAuthRequest request;
		
		//We create the url to get user's followers
		String url;
		
		String formated = createUrlArgsFromString(mess);
		target = createUrlArgsFromString(target);
		
		url = "https://api.twitter.com/1.1/direct_messages/new.json?screen_name="+target+"&text="+formated;
		request = new OAuthRequest(Verb.POST, url);
		AuthentificationService.signRequest(accessToken, request);
		Response r = request.send();

	}
	
	/**
	 * Retweet the specified Tweet 
	 * @param id Tweet's ID
	 */
	public static void retweet(String id) throws Exception{	
		String url;
		OAuthRequest request;
		
		//We create the url
		id = createUrlArgsFromString(id);
		url = Res.domain+"statuses/retweet/"+id+".json";
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We sign the request
		AuthentificationService.signRequest(accessToken, request);
		
		//We send the request
		Response r = request.send();
		
		//We verify that the the Tweet was retweeted
		try{
			Tweet t = gson.fromJson(r.getBody(), Tweet.class);
			
			if(!id.equals(t.getID()))
				throw new Exception("The tweet wasn't retweeted");
			
		}catch(Exception e){
			throw new Exception("this tweet does'nt exist");
		}
		System.out.println(r.getBody());
	}
	
	/**
	 * To follow somebody
	 * @param followed
	 * @throws Exception
	 */
	public static void follow(String followed) throws Exception{
		String url;
		OAuthRequest request;
		User verif;
		Response r;
		
		//We create the url
		followed = createUrlArgsFromString(followed);
		// TODO: Use Res for this String
		url = "https://api.twitter.com/1.1/friendships/create.json?screen_name="+followed;
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We sign the request
		AuthentificationService.signRequest(accessToken, request);
		
		//We execute the request
		r = request.send();
		
		//We create an object User to verify that he's followed
		try{
			verif = gson.fromJson(r.getBody(), User.class);
			
			if(!followed.equals(verif.getName()))
				throw new Exception("L'utilisateur ne peut être suivi");
			
		}catch(Exception e){
			throw new Exception("Impossible to follow this user");
		}
	}
	
	/**
	 * Make a tweet a favorite
	 * @param id ID of the tweet to set as favorite
	 */
	public static void setFavorite(String id) throws Exception{
		String url;
		OAuthRequest request;
		Response r;
		Tweet verif; //To verify if the tweet was set as favourite
		
		//We create the url
		id = createUrlArgsFromString(id);
		url = Res.domain+"favorites/create/"+id+".json";
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We signed the url
		AuthentificationService.signRequest(accessToken, request);
		
		//We execute the request
		r = request.send();
		
		//We verify if the tweet was correctly send
		try{
			verif = gson.fromJson(r.getBody(), Tweet.class);
			
			if(!id.equals(verif.getID())){
				throw new Exception("The tweet was'nt set as favourite");
			}
		}catch(Exception e){
			throw new Exception ("The tweet does'nt exist");
		}
	}
	
	/**
	 * Destroy a favorite
	 * @param id ID of the tweet to remove from favorites
	 */
	public static void removeFavorite(String id) throws Exception{
		String url;
		OAuthRequest request;
		Response r;
		Tweet verif; //To verify if the tweet was remove from favorites
		
		//We create the url
		id = createUrlArgsFromString(id);
		url = Res.domain+"favorites/destroy/"+id+".json";
		
		//We create the request
		request = new OAuthRequest(Verb.POST, url);
		
		//We signed the url
		AuthentificationService.signRequest(accessToken, request);
		
		//We execute the request
		r = request.send();
		
		//We verify if the tweet was correctly send
		try{
			verif = gson.fromJson(r.getBody(), Tweet.class);
			
			if(!id.equals(verif.getID())){
				throw new Exception("The tweet was'nt remove from favourites");
			}
		}catch(Exception e){
			throw new Exception ("The tweet does'nt exist");
		}
	}
}
