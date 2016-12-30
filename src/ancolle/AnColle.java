/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancolle;

import ancolle.ui.ProductView;
import ancolle.ui.AlbumView;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

    private final Settings settings;
    private final ProductView productView;
    private final AlbumView albumView;
    private final VBox root;
    private final VBox mainContent;
    private final ScrollPane scrollPane;
    private final Tab mainTab;
    private final TabPane tabPane;
    private static final Background AZURE_BACKGROUND = new Background(
            new BackgroundFill(Color.AZURE, null, null));

    public AnColle() {
        super();
        this.root = new VBox();
        this.mainContent = new VBox();
        this.scrollPane = new ScrollPane();
        this.productView = new ProductView(this);
        this.albumView = new AlbumView(this);
        this.mainTab = new Tab("Explorer", mainContent);
        this.mainTab.setClosable(false);
        this.tabPane = new TabPane();
        this.tabPane.getTabs().add(mainTab);
        this.settings = IO.loadSettings();
    }

    public Tab newTab(String title, Node content) {
        Tab tab = new Tab(title, content);
        tabPane.getTabs().add(tab);
        return tab;
    }

    public void setSelectedTab(Tab tab) {
        tabPane.getSelectionModel().select(tab);
    }

    public void viewProducts() {
        scrollPane.setContent(productView);
        albumView.cancelQueuedTasks();
    }

    public void view(Product product) {
        scrollPane.setContent(albumView);
        productView.cancelQueuedTasks();
        albumView.setProduct(product);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuBar menu = new MenuBar();
        root.getChildren().add(menu);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().add(tabPane);

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

        productView.setBackground(AZURE_BACKGROUND);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.setOnKeyPressed(evt -> {
            if (tabPane.getSelectionModel().getSelectedItem() == mainTab) {
                if (evt.getCode() == KeyCode.ESCAPE) {
                    viewProducts();
                } else if (evt.getCode() == KeyCode.S) {
                    saveSettings();
                }
            }
        });
        mainContent.getChildren().add(scrollPane);

        mainContent.setBackground(AZURE_BACKGROUND);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        mainTab.setContent(mainContent);
        viewProducts();

        // Load and display tracked products in the background
        Platform.runLater(() -> {
            settings.trackedProducts.forEach((id) -> {
                productView.addProductById(id);
            });
        });

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("AnColle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.FINE);
        Logger.getGlobal().addHandler(new ConsoleHandler());
        launch(args);
    }

    private void saveSettings() {
        IO.saveSettings(settings);
    }

    public void addTrackedProduct(int id) {
        settings.trackedProducts.add(id);
        productView.addProductById(id);
        saveSettings();
    }

}
