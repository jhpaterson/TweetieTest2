import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Define a Personalized popup, Used to send a Tweet
 * @author Vincent
 *
 */
public class PopupPerso{
	private Stage popup;
	private Scene scene;
	private StackPane root;
	
	@FXML private VBox mainVbox;
	@FXML private   HBox header;
	@FXML private       Text numberOfCharacters; //Number of characters you can write in a tweet
	@FXML private   	HBox to_zone; //For private message
	@FXML private 			TextField to;
	@FXML private 		HBox tweet_zone;
	@FXML private 			TextArea text;
	@FXML private 		HBox send_zone;
	@FXML private 			Button btnCancel;
	@FXML private 			Button btnSend;
	@FXML private   HBox footer;
	
	private double initialX;
	private double initialY;
	
	
	public PopupPerso(String name) throws Exception{	
		popup = new Stage();
		
		Parent fxml = FXMLLoader.load(getClass().getResource(name));
		
		root = new StackPane();
		root.setId("root");
		
		scene = new Scene(root);
		
		root.getChildren().add(fxml);
		
		popup.initStyle(StageStyle.UNDECORATED);
		popup.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		popup.setResizable(true);
		popup.setScene(scene);
		popup.show();
		
		
        mainVbox = (VBox) root.lookup("#mainVbox");
        	header = (HBox) scene.lookup("#menuBar");
        		numberOfCharacters = (Text) scene.lookup("#charRemaining");
        		if(numberOfCharacters != null)
        			numberOfCharacters.setText("140");
        	to_zone = (HBox) scene.lookup("#to_zone");
				to = (TextField) scene.lookup("#to");
    		tweet_zone = (HBox) scene.lookup("#text_to_send");
    			text = (TextArea) scene.lookup("#tweet");
    		send_zone = (HBox) scene.lookup("#send");
    			btnCancel = (Button) scene.lookup("#btnCancel");
    			btnSend = (Button) scene.lookup("#btnSend");
    		footer = (HBox) scene.lookup("#footer");
		
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				popup.close();
			}
		});
		
		if(name.equals("tweet_popup.fxml"))
			this.setBindingsTweet();
		else
			this.setBindingsPM();
		
		
		addDraggableNode(mainVbox);
		
	}
	
	private void setBindingsTweet(){
		btnSend.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				//We send the string contained in the textarea
				TwitterAPI.send_tweet(text.getText());
				popup.close();
			}
		});
		
		text.setOnKeyReleased(new EventHandler <KeyEvent>(){
			@Override
			public void handle(KeyEvent event){
				//We count the number of characters of the text
				String s = text.getText();
				int caracteres = 140 - s.length();
				
				//And then we change the display
				numberOfCharacters.setText(caracteres+"");
			}
		});
	}
	
	/**
	 * Set binding private message
	 */
	private void setBindingsPM(){
		btnSend.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				//We send the message
				TwitterAPI.sendMessage(text.getText(), to.getText());
				popup.close();
			}
		});
	}
	
	/**
	 * Getters
	 * 
	 */
	public Stage getStage(){
		return popup;
	}
	
	public Scene getScene(){
		return scene;
	}
	
	/**
	 * Same as in Landpage.java
	 * @param node a Node
	 */
	private void addDraggableNode(final Node node) {

	    node.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent me) {
	            if (me.getButton() != MouseButton.MIDDLE) {
	                initialX = me.getSceneX();
	                initialY = me.getSceneY();
	            }
	        }
	    });

	    node.setOnMouseDragged(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent me) {
	            if (me.getButton() != MouseButton.MIDDLE) {
	                node.getScene().getWindow().setX(me.getScreenX() - initialX);
	                node.getScene().getWindow().setY(me.getScreenY() - initialY);
	            }
	        }
	    });
	}
}