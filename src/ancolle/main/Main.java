package ancolle.main;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Program entrypoint
 *
 * @author samuel
 */
public class Main {

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.FINE);
        Logger.getGlobal().addHandler(new ConsoleHandler());
        AnColle.launch(args);
    }

}
