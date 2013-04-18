
/**
 * Entities from a tweet ( meta-info )
 * @author Geoffrey
 *
 */
public class Entities {

	private Hashtags hashtags[];
	private Urls urls[];
	private User_mentions user_mentions[];
	
	public String toString(){
		String retour = "---Entities : ";
		if(hashtags!=null){
			for(Hashtags h : hashtags){
				retour+= h.toString()+"|";
			}
			
		}
		if(urls!=null){
			for(Urls u : urls){
				retour+=u.toString()+"|";
			}
			
		}
		if(user_mentions!=null){
			for(User_mentions um : user_mentions){
				retour+= um.toString();
			}
			
		}
		retour+="---";
		
		return retour;
	}

}
