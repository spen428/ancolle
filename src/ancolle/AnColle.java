/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancolle;

import ancolle.ui.ProductView;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author samuel
 */
public class AnColle extends Application {
    
    private static final int[] tracked_products = {4277, 1757};
    private static final Product prod = VGMdbAPI.getProductById(1757);

    @Override
    public void start(Stage primaryStage) {        
        VBox root = new VBox();
        ProductView productView = new ProductView(prod);
        productView.setBackground(new Background(
                new BackgroundFill(Color.AZURE, null, null)));
        VBox.setVgrow(productView, Priority.ALWAYS);
        root.getChildren().add(productView);
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("AnColle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.INFO);
        Logger.getGlobal().addHandler(new ConsoleHandler());
        launch(args);
    }

}
