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
package ancolle.ui;

import ancolle.items.Product;
import ancolle.main.Settings;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The root {@link Node} of the main {@link Scene} of the {@link Application}
 *
 * @author lykat
 */
public class AnColle extends VBox {

    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(AnColle.class.getName());

    private static final String PRODUCT_TRACKER_TAB_TITLE = "Product Tracker";

    private final Settings settings;
    private final ProductView productView;
    private final AlbumView albumView;
    private final StatusBar statusBar;
    private final ScrollPane productViewScrollPane;
    private final ScrollPane albumViewScrollPane;
    private final Tab productViewTab;
    private final TabPane tabPane;

    private Window mainWindow = null;
    private final Tab albumViewTab;

    public AnColle(Stage stage) {
	super();
	this.mainWindow = stage;

	this.productViewScrollPane = new ScrollPane();
	this.albumViewScrollPane = new ScrollPane();
	this.productView = new ProductView(this);
	this.albumView = new AlbumView(this);
	this.productViewTab = new Tab(PRODUCT_TRACKER_TAB_TITLE,
		productViewScrollPane);
	this.tabPane = new TabPane();
	this.settings = new Settings();
	this.statusBar = new StatusBar();

	productViewTab.setClosable(false);
	tabPane.getTabs().addAll(productViewTab);
	tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
	settings.load();

	setId("root");

	// MENU BAR //
	MenuBar menu = new MenuBar();
	getChildren().add(menu);

	Menu menuFile = new Menu("File");
	menuFile.setId("menu-file");
	menu.getMenus().add(menuFile);

	Menu menuEdit = new Menu("Edit");
	menuEdit.setId("menu-edit");
	menu.getMenus().add(menuEdit);

	Menu menuView = new Menu("View");
	menuView.setId("menu-view");
	menu.getMenus().add(menuView);
	CheckMenuItem menuItemShowHiddenItems = new CheckMenuItem("Show hidden items");
	menuItemShowHiddenItems.setId("menu-item-show-hidden-items");
	menuItemShowHiddenItems.setSelected(getSettings().isShowHiddenItems());
	menuItemShowHiddenItems.setOnAction(evt -> {
	    getSettings().setShowHiddenItems(!settings.isShowHiddenItems());
	    productView.updateHiddenItems();
	    albumView.updateHiddenItems();
	});
	menuView.getItems().add(menuItemShowHiddenItems);

	Menu menuTools = new Menu("Tools");
	menuTools.setId("menu-tools");
	menu.getMenus().add(menuTools);

	Menu menuHelp = new Menu("Help");
	menuHelp.setId("menu-help");
	menu.getMenus().add(menuHelp);

	VBox.setVgrow(tabPane, Priority.ALWAYS);
	getChildren().add(tabPane);
	tabPane.setOnKeyPressed(evt -> {
	    if (evt.getCode() == KeyCode.W && evt.isControlDown()) {
		int idx = tabPane.getSelectionModel().getSelectedIndex();
		if (idx != 0) {
		    closeTab(tabPane.getTabs().get(idx));
		}
	    }
	});

	getChildren().add(statusBar);

	productViewTab.setId("product-view-tab");

	productViewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	productViewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	VBox.setVgrow(productViewScrollPane, Priority.ALWAYS);
	productViewScrollPane.setContent(productView);

	productViewTab.setContent(productViewScrollPane);

	albumViewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	albumViewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	VBox.setVgrow(albumViewScrollPane, Priority.ALWAYS);
	albumViewScrollPane.setContent(albumView);

	this.albumViewTab = new Tab("", albumViewScrollPane);
	albumViewTab.setId("album-view-tab");
	albumViewTab.setContent(albumViewScrollPane);
	albumViewTab.setOnClosed(evt -> {
	    albumView.cancelQueuedTasks();
	    albumView.setProduct(null);
	    selectTabToRight();
	});

	setOnKeyPressed(evt -> {
	    if (tabPane.getSelectionModel().getSelectedItem() == productViewTab) {
		switch (evt.getCode()) {
		    case A:
			productView.doAddProductDialog();
			break;
		    default:
			break;
		}
	    }
	});

	// Load and display tracked products in the background
	Platform.runLater(() -> {
	    getSettings().trackedProductIds.forEach((id) -> {
		productView.addProductById(id);
	    });
	});

	mainWindow.setOnCloseRequest(evt -> {
	    saveSettings();
	});
    }

    /**
     * Create a new tab and add it to the tab pane. If a tab with the same title
     * and content already exists, a new tab will not be created and a reference
     * to the existing tab will instead be returned. The existing tab will also
     * be highlighted visually on screen.
     *
     * @param title the tab title
     * @param content the tab content
     * @return the newly created tab
     */
    public Tab newTab(String title, Node content) {
	for (Tab tab : tabPane.getTabs()) {
	    if (tab.getText().equals(title) && tab.getContent().equals(content)) {
		LOG.log(Level.INFO, "Not adding duplicate tab");
		flashTab(tab);
		return tab;
	    }
	}

	Tab tab = new Tab(title, content);
	tab.setOnClosed(evt -> {
	    selectTabToRight();
	});
	tabPane.getTabs().add(tab);
	return tab;
    }

    private void selectTabToRight() {
	int selected = tabPane.getSelectionModel().getSelectedIndex();
	if (tabPane.getTabs().size() > 1
		&& selected + 1 < tabPane.getTabs().size()) {
	    setSelectedTab(tabPane.getTabs().get(selected + 1));
	}
    }

    private void flashTab(Tab tab) {

    }

    public void setSelectedTab(Tab tab) {
	tabPane.getSelectionModel().select(tab);
	tab.getContent().requestFocus();
    }

    /**
     * Close a tab contained within the main tab pane. This is better than
     * calling {@code remove()} on {@link TabPane#getTabs} because it propagates
     * a close event to the tab, ensuring any event handlers still execute.
     *
     * @param tab the tab
     */
    private void closeTab(Tab tab) {
	tabPane.fireEvent(new Event(tabPane, tab, Tab.TAB_CLOSE_REQUEST_EVENT));
    }

    /**
     * Save program settings.
     */
    private void saveSettings() {
	getSettings().save();
    }

    /**
     * Add a {@link Product} to the tracked products page. The newly created
     * {@link ProductNode} will be automatically highlighted and scrolled into
     * view.
     *
     * @param id the {@link Product} id
     */
    public void addTrackedProduct(int id) {
	getSettings().trackedProductIds.add(id);
	productView.addProductById(id, true);
	saveSettings();
    }

    /**
     * Set the main content view to the {@link ProductView} page
     */
    public void viewProducts() {
	setSelectedTab(productViewTab);
    }

    /**
     * Set the main content view to the {@link AlbumView} page, and display the
     * given product on it
     *
     * @param product the product
     */
    public void view(Product product) {
	if (albumView.getProduct() != product) {
	    albumView.cancelQueuedTasks();
	    albumView.setProduct(product);
	}
	if (!tabPane.getTabs().contains(albumViewTab)) {
	    tabPane.getTabs().add(1, albumViewTab);
	}
	albumViewTab.setText(product.title_en);
	setSelectedTab(albumViewTab);
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
	return settings;
    }

    /**
     * Return the "main window" of this application, i.e. the primary stage that
     * was passed to the {@link Application#start} method.
     *
     * @return the main window of this application
     */
    public Window getMainWindow() {
	return mainWindow;
    }

    /**
     * Scroll the given {@link Node} into view.
     *
     * @param node the node
     */
    public void scrollIntoView(Node node) {
	double w = productViewScrollPane.getContent().getBoundsInLocal().getWidth();
	double h = productViewScrollPane.getContent().getBoundsInLocal().getHeight();
	double x = node.getBoundsInParent().getMaxX();
	double y = node.getBoundsInParent().getMaxY();
	productViewScrollPane.setVvalue(y / h);
	productViewScrollPane.setHvalue(x / w);
    }

}
