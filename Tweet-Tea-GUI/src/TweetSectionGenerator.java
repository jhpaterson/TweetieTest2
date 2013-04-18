import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.codec.StringEncoderComparator;

import netscape.javascript.JSObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.sun.javafx.scene.layout.region.BackgroundFill;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


import org.apache.commons.*;

//TODO : add addFirst() to automaticaly inject new tweets (whithout refreshing manualy)


/**
 * Is the controler of the center area, where we see tweets
 * @author Geoffrey
 * @see the word document why we use a webview instead of JavaFx components
 *
 */
public class TweetSectionGenerator {

	
	/**
	 * The webview to manage
	 */
	WebView view;
	/**
	 * The webengine associated
	 */
	WebEngine engine;
	
	/**
	 * CSS of the view
	 */
	private String css;
	
	/**
	 * JQuery
	 */
	private String jQuery;
	
	/**
	 * Main.js
	 */	
	private String mainJs;
	
	/**
	 * The google JSON parser
	 */
	Gson serializer ;

	
	public TweetSectionGenerator(){
		
		serializer = new Gson();

		view = new WebView();
		engine = view.getEngine();
		
		try {
			
			/* Warning !!!
			 * getClass().getResource("res/css/tweets.css").toExternalForm()
			 * Give a string like that : file:c:/path/to/file.ext  if java is running from classical .class file
			 * And give rsrc:path/to/file.ext if run in a .jar
			 * Be sure to remove "file:" or "rscr:"
			 */
			
			String pathToCss = getClass().getResource("/res/css/tweets.css").toExternalForm().replaceAll("file:", "").replaceAll("rsrc:", "");
			String pathTojQuery = getClass().getResource("/res/js/jquery-1.9.1.min.js").toExternalForm().replaceAll("file:", "").replaceAll("rsrc:", "");
			String pathToJs = getClass().getResource("/res/js/main.js").toExternalForm().replaceAll("file:", "").replaceAll("rsrc:", "");
			
			css = readFileAsString(pathToCss);
			jQuery = readFileAsString(pathTojQuery);
			mainJs = readFileAsString(pathToJs);
			
			
					
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
		
		
		// we build the default page's body
		StringBuffer s= new StringBuffer("<!doctype html>"  // html 5 of course 
				+" <html>"
				+"		<head>" 
				+			"<style type='text/css'>" 
				+				 css
				+			"</style>"
				+			"<script type='text/javascript'>"
				+				jQuery
				+			"</script>"
				+			"<script type='text/javascript'>"		// need to be loaded after jQuery 
				+				mainJs
				+			"</script>"
				+		"</head>"
				+"		<body>");
		
		
		s.append("			<script type='text/javascript'>"
				+				"initialize();"
				+			"</script>"
				+	"</body>"
				+"</html>");
		
		engine.loadContent(s.toString());
		

	}
		
	
	
	/**
	 * Add a tweet to the webview ( but don't show it )
	 * @param tweet
	 */
	public void addTweet(Tweet tweet){
				
		String json = serializer.toJson(new JsonWrapper(tweet));			// beware to the JsonWrapper !!
		addBySendingToJS(json);   // we send the tweet to Javascript
	}
	
	/**
	 * Clear all the tweets in webview but keep the page loaded and the scripts lanched
	 * This is better than to regenerate new webview
	 */
	public void clear(){
		engine.executeScript("clearAll()");
	}
	
	
	

	/**
	 * Send a JSON tweet to JS. 
	 * @param json
	 */
	private void addBySendingToJS(String json){
		
		//json= json.replace("\\", "\\\\");
		// we need to translate some chars in HTML entities
		json = json.replace("'", "&apos;'").replace("\\\"", "&quot;");  // replace ' by \' and " by \" jQuery will automaticaly replace all on the JS side
		json = json.replace("\n", "<br/>");
		json = json.replace("\\n", "<br/>");
		//System.out.println(json);
	
		try{
			engine.executeScript("add('"+json+"')");
		}
		catch(Exception e){
			System.out.println("Error while injecting in webView : " +e.getMessage());
		}
		
		
	}
	
	/**
	 * Ask the JS to show tweets
	 * Useful because you can show the tweets after they are loaded
	 * If you show the tweet as the same time as you inject it , the webview slows
	 */
	public void showTweets(){
		engine.executeScript("showTweets()");
	}

	
	/**
	 * Equeivalent of PHP get_file_content();
	 * Read a text file into a String
	 * @param filePath 
	 * @return	String
	 * @throws IOException
	 */
	private String readFileAsString(String filePath) throws IOException {
	
       StringBuffer buffer = new StringBuffer();
       String line;
		try{
			BufferedReader fich = new BufferedReader(new FileReader(filePath));
			
			try{
				while((line = fich.readLine())!=null){
			    	   buffer.append(line);
			    	  
			       }
			}
			finally{
				fich.close();
			}
			
		       
		}
		catch(Exception e){
			throw new IOException("Tweets's CSS not found/loaded");
		}
		
       return buffer.toString();
    }
	
//	public boolean isReady(){
//		
//		boolean isready= false;
//		Worker worker = engine.getLoadWorker();
//		if(worker.getState()==Worker.State.SUCCEEDED)		return true;
//		else return false;
//		
//		// ok ok c'est pas propre mais au moins c'est pas ambiguë
//		
//		
//	}
	
	
	/**
	 * Append text ( Not Tweet ) to the webview. Be sure it's JSON , else > JSException or worth : nothing
	 * @param text
	 */
	public void appendText(String text){
		addBySendingToJS(text);
	}
	
	
	
	/**
	 * Getters
	 * 
	 */
	
	public WebView  getWebView(){
		return view;
	}
	
	
	
	
	
}
