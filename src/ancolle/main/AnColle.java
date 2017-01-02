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

import ancolle.items.Product;
import ancolle.ui.AlbumView;
import ancolle.ui.ProductView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The JavaFX application class
 *
 * @author samuel
 */
public class AnColle extends Application {

    private static final Background AZURE_BACKGROUND = new Background(
	    new BackgroundFill(Color.AZURE, null, null));

    public final Settings settings;
    private final ProductView productView;
    private final AlbumView albumView;
    private final VBox root;
    private final VBox mainContent;
    private final StatusBar statusBar;
    private final ScrollPane scrollPane;
    private final Tab mainTab;
    private final TabPane tabPane;

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
	this.settings = new Settings();
	this.statusBar = new StatusBar();

	this.tabPane.getTabs().add(mainTab);
	this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
	this.settings.load();
    }

    /**
     * Create a new tab and add it to the tab pane
     *
     * @param title the tab title
     * @param content the tab content
     * @return the newly created tab
     */
    public Tab newTab(String title, Node content) {
	Tab tab = new Tab(title, content);
	tabPane.getTabs().add(tab);
	return tab;
    }

    public void setSelectedTab(Tab tab) {
	tabPane.getSelectionModel().select(tab);
    }

    /**
     * Save program settings.
     */
    private void saveSettings() {
	settings.save();
    }

    /**
     * Add a {@link Product} to the tracked products page
     *
     * @param id the {@link Product} id
     */
    public void addTrackedProduct(int id) {
	settings.trackedProductIds.add(id);
	productView.addProductById(id);
	saveSettings();
    }

    /**
     * Set the main content view to the {@link ProductView} page
     */
    public void viewProducts() {
	scrollPane.setContent(productView);
	albumView.cancelQueuedTasks();
    }

    /**
     * Set the main content view to the {@link AlbumView} page, and display the
     * given product on it
     *
     * @param product the product
     */
    public void view(Product product) {
	scrollPane.setContent(albumView);
	productView.cancelQueuedTasks();
	albumView.setProduct(product);
    }

    @Override
    public void start(Stage primaryStage) {
	// MENU BAR //
	MenuBar menu = new MenuBar();
	root.getChildren().add(menu);

	Menu menuFile = new Menu("File");
	menu.getMenus().add(menuFile);

	Menu menuEdit = new Menu("Edit");
	menu.getMenus().add(menuEdit);

	Menu menuView = new Menu("View");
	menu.getMenus().add(menuView);
	CheckMenuItem menuItemShowHiddenItems = new CheckMenuItem("Show hidden items");
	menuItemShowHiddenItems.setSelected(settings.isShowHiddenItems());
	menuItemShowHiddenItems.setOnAction(evt -> {
	    settings.setShowHiddenItems(!settings.isShowHiddenItems());
	    productView.updateHiddenItems();
	    albumView.updateHiddenItems();
	});
	menuView.getItems().add(menuItemShowHiddenItems);

	Menu menuTools = new Menu("Tools");
	menu.getMenus().add(menuTools);

	Menu menuHelp = new Menu("Help");
	menu.getMenus().add(menuHelp);

	VBox.setVgrow(tabPane, Priority.ALWAYS);
	root.getChildren().add(tabPane);

	root.getChildren().add(statusBar);

	productView.setBackground(AZURE_BACKGROUND);

	scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	scrollPane.setFitToWidth(true);
	scrollPane.setFitToHeight(true);
	VBox.setVgrow(scrollPane, Priority.ALWAYS);

	root.setOnKeyPressed(evt -> {
	    if (tabPane.getSelectionModel().getSelectedItem() == mainTab) {
		switch (evt.getCode()) {
		    case ESCAPE:
			viewProducts();
			break;
		    case S:
			saveSettings();
			break;
		    case A:
			productView.doAddProductDialog();
			break;
		    default:
			break;
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
	    settings.trackedProductIds.forEach((id) -> {
		productView.addProductById(id);
	    });
	});

	Scene scene = new Scene(root, 1280, 720);
	primaryStage.setTitle("AnColle");
	primaryStage.setScene(scene);
	primaryStage.show();
    }

}
