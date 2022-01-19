package ancolle.main;

import ancolle.ui.ApplicationRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JavaFX application class
 *
 * @author lykat
 */
public class Main extends Application {

	public static final String APPLICATION_NAME = "AnColle";

	public static final String VERSION = "0.5a";

	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	public static final Settings settings = new Settings();

	@Override
	public void start(Stage primaryStage) throws Exception {
		Main.settings.reload();

		Logger.getGlobal().setLevel(Level.ALL);
		Logger.getGlobal().addHandler(new ConsoleHandler());

		ApplicationRoot root = new ApplicationRoot(primaryStage);
		Scene scene = new Scene(root, 1280, 720);
		scene.getStylesheets().add("stylesheet.css");

		primaryStage.setScene(scene);
		primaryStage.setTitle(APPLICATION_NAME + " " + VERSION);
		primaryStage.show();
	}
}
