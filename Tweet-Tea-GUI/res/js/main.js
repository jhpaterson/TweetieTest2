/*
###########################################################################################

Warning! We have here a particular version of WebKit and there are some particularities:
	- Only /* *\/ comments are allowed -> if you use // -> JSException at startup
	- You NEED to run the js manualy at startup , else, variables & function stay 'undefined'
	- We are here on a bad JS platform, we need to act like a programmer in the 70's : save memory & process time ^^ 

###########################################################################################
*/

/*	Globals */

var twitterPrefix="https://twitter.com/";

/*
	An array of tweets... can be usefull... ( sorting  or other...)
*/
var tabTweet = [];



/*________________________*/


/* initialize the webview by showing a prompt text and make JS working correctly */
function initialize(){
	$("body").append("<div id='promptStartText' class='tweetBloc'></div>");
	$(".tweetBloc").eq(0).text("Click Home or Search something to Start");
}


/*
	Remove all tweets
*/
function clearAll(){
	$('body').empty();
}

/*
	Add a JSON tweet to the view (but by default it is hidden)
*/
function add( json ){
	
	json = json.replace("\\\"","\"");
	json = json.replace("\\'","'");
	/*json = json.replace("\\\n","\n");*/

	var tweetBloc = makeTweetBloc(json);

	$("body").append(tweetBloc);
}

function makeTweetBloc(jsonTweet){

		try{
			var jsonTweet = JSON.parse(jsonTweet);	
		}
		catch(e){
			/* If a tweet can't be parsed we inhect the error in the view for the moment. By this way , we can see it*/
			return e;
		}
		
		
		var  id = jsonTweet.id;
		
		var  name = jsonTweet.name;
		var  screen_name= jsonTweet.screen_name;
		var  text = jsonTweet.text;
		var  time = jsonTweet.created_at;
		var  pictureURL =  jsonTweet.pictureUrl;
		var twitterPrefix = jsonTweet.twitterPrefix;

		var profilUrl = twitterPrefix+screen_name;
		
		
		var  s = 	"<div class='tweetBloc' id='"+id+"'>"
										  +"<table>"	
										  +		"<tr>"
										  +		"<td></td>"
										  +			"<td colspan='2' >"
										  +				"<a class='name' href='"+profilUrl+"'>"+name+"</a>"
										  +				"&nbsp;&nbsp;"	/* soit deux espaces */
										  +				"<span class='screenName'>"
										  +					"<a href='"+profilUrl+"'>"+"@"+screen_name+"</a></span>"
										  +			"</td>"	
										  +			"<td class='time'>"
										  /*+				tweet.getDate()   <-----   To do*/
										  +					"00:00"
										  +			"</td>"
										  +		"</tr>"
										  +		"<tr>"
										  +			"<td class='userPictureTD' rowspan='3'>"
										  
										  +					"<img class='userPicture' src='"+pictureURL+"'/>"
										  
										  +			"</td>"
										  +			"<td class='message' colspan='3'>"
										  +				text
										  +			"</td>"										 
										  +		"</tr>"
										  +		"<tr>"
										  +			"<td >"
										  +				"<table><tr class='options' ><td>Add favorite</td><td>opt2</td></tr></table>"
										  +			"</td>"
										  +		"</tr>"
										  +		"<tr class='replyContainer'>"
										  +		"</tr>"
										  +	"</table>"
										  +"</div>";
		return s;
	}


/*Add the respond tools to a tweet*/

function appendRespondTools( domObject ){

	var tweet = $(domObject);
	$(tweet).addClass("replying");
	var html = 	"<td class='respondBar'>"
				
				+		"<textarea rows='1' cols='55'>"
				+	 		"Reply to "+$(tweet).find(".screenName").text()
				+		"</textarea>"
				
				+"</td>";
	$(tweet).find(".replyContainer").html(html);
	
}



function showTweets(){
		
	formatTweets();
	$(".tweetBloc").css("visibility","visible");
	
}


/*Make the tweetBlocs usables, we add bindings, links etc...*/
function formatTweets(){

	$(".tweetBloc").each(function(){

		var link = $(this).find(".screenName").text();
		var a = "<a href='"+link+"'></a>";
		
		$(this).find(".name").wrap(a);


		/*Binding for tweet paddings and animations*/

		$(this).on("click", function(event){
			event.stopPropagation();
			$(this).siblings().animate({
				"margin": "0",
				"border-radius": "0px"
			},200);
			$(this).animate({
				"margin": "5px 0 5px 0",
				"border-radius": "10px"
			},200);

			if(!$(this).hasClass('replying')){
				appendRespondTools(this);
			}
			
		});


		/* bindings des options*/
		var optionRow = $(this).find(".options");
		$(this).on("mouseenter", function(event){
			$(optionRow).css("opacity","1");
		});

		$(this).on("mouseleave", function(event){
			$(optionRow).css("opacity","0.01");
		});

	});


}

