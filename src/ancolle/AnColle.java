/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancolle;

import ancolle.ui.ProductView;
import ancolle.ui.AlbumView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
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

    private static final int[] tracked_products = {4277, 3270, 1757, 3939, 2981};

    private final AtomicInteger taskCount = new AtomicInteger(0);
    public final ExecutorService executor = Executors.newCachedThreadPool(
            runnable -> {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                return thread;
            });

    private final ProductView productView;
    private final AlbumView albumView;
    private final VBox root;
    private final ScrollPane scrollPane;

    public AnColle() {
        super();
        this.root = new VBox();
        this.scrollPane = new ScrollPane();
        this.productView = new ProductView(this);
        this.albumView = new AlbumView(this);
    }

    public void viewProducts() {
        albumView.cancelQueuedTasks();
        scrollPane.setContent(productView);
    }

    public void view(Product product) {
        scrollPane.setContent(albumView);
        albumView.setProduct(product);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuBar menu = new MenuBar();
        root.getChildren().add(menu);

        Menu menuFile = new Menu("File");
        menu.getMenus().add(menuFile);

        Menu menuEdit = new Menu("Edit");
        menu.getMenus().add(menuEdit);

        Menu menuView = new Menu("View");
        menu.getMenus().add(menuView);

        Menu menuTools = new Menu("Tools");
        menu.getMenus().add(menuTools);

        Menu menuHelp = new Menu("Help");
        menu.getMenus().add(menuHelp);

        for (int id : tracked_products) {
            productView.addProductById(id);
        }
        productView.setBackground(new Background(
                new BackgroundFill(Color.AZURE, null, null)));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        viewProducts();
        root.getChildren().add(scrollPane);

        root.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.ESCAPE) {
                viewProducts();
            }
        });
        root.setBackground(new Background(
                new BackgroundFill(Color.AZURE, null, null)));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

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
