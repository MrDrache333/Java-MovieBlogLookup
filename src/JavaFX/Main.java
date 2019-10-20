package JavaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The type Main.
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
		primaryStage.setTitle("MovieBlogLookup");
		primaryStage.setScene(new Scene(root, 1480, 700));
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
