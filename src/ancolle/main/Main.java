/*  AnColle, an anime and video game music collection tracker
 *  Copyright (C) 2016-17  lykat
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ancolle.main;

import ancolle.ui.AnColle;
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

	/**
	 * The name of the application
	 */
	public static final String APPLICATION_NAME = "AnColle";

	/**
	 * Program version string. This does not change between builds, only between
	 * releases.
	 */
	public static final String VERSION = "0.4a";

	/**
	 * The logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	@Override
	public void start(Stage primaryStage) throws Exception {
		Logger.getGlobal().setLevel(Level.ALL);
		Logger.getGlobal().addHandler(new ConsoleHandler());

		AnColle ancolle = new AnColle(primaryStage);
		Scene scene = new Scene(ancolle, 1280, 720);
		scene.getStylesheets().add("stylesheet.css");

		primaryStage.setScene(scene);
		primaryStage.setTitle(APPLICATION_NAME + " " + VERSION);
		primaryStage.show();
	}

}
