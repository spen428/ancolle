package ancolle.ui;

import ancolle.items.Artist;
import ancolle.items.Item;
import ancolle.items.Product;
import ancolle.main.Main;
import ancolle.ui.concurrency.AnColleTaskManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationRoot extends VBox {

	private static final Logger LOG = Logger.getLogger(ApplicationRoot.class.getName());

	public final Window mainWindow;
	private final ProductView productView;
	private final ProductAlbumView productAlbumView;
	private final DiscogView discogView;
	private final ViewTab productViewTab;
	private final TabPane tabPane;
	private final ViewTab albumViewTab;
	private final ViewTab discogViewTab;
	private final Set<Tab> flashingTabs;
	private final AnColleTaskManager taskManager;
	private final ArtistView artistView;

	public ApplicationRoot(Stage stage) {
		super();
		this.flashingTabs = new HashSet<>(4);
		this.mainWindow = stage;
		this.productAlbumView = new ProductAlbumView(this);
		this.discogView = new DiscogView(this);
		this.productView = new ProductView(this);
		artistView = new ArtistView(this);
		this.albumViewTab = createTab("", this.productAlbumView, null);
		this.discogViewTab = createTab("", this.discogView, null);
		this.productViewTab = createTab("Product Tracker", this.productView, null);
		ViewTab artistViewTab = createTab("Artist Tracker", artistView, null);
		this.tabPane = new TabPane();
		StatusBar statusBar = new StatusBar();
		this.taskManager = new AnColleTaskManager(statusBar);

		productViewTab.setClosable(false);
		artistViewTab.setClosable(false);
		tabPane.getTabs().addAll(productViewTab, artistViewTab);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

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
		menuItemShowHiddenItems.setSelected(Main.settings.isShowHiddenItems());
		menuItemShowHiddenItems.setOnAction(evt -> {
			Main.settings.setShowHiddenItems(!Main.settings.isShowHiddenItems());
			productView.updateHiddenItems();
			productAlbumView.updateHiddenItems();
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
				Tab tab = tabPane.getSelectionModel().getSelectedItem();
				if (tab != productViewTab) {
					closeTab(tab);
				}
			} else if (evt.getCode() == KeyCode.F5
					|| (evt.getCode() == KeyCode.R && evt.isControlDown())) {
				Tab tab = tabPane.getSelectionModel().getSelectedItem();
				if (tab == productViewTab) {
					productView.refreshItems();
				} else if (tab == albumViewTab) {
					productAlbumView.refreshItems();
				} else {
					// TODO
				}
			}
		});

		getChildren().add(statusBar);

		productViewTab.setId("product-view-tab");
		artistView.setId("artist-view-tab");
		albumViewTab.setId("album-view-tab");
		albumViewTab.setOnClosed(evt -> {
			productAlbumView.cancelQueuedTasks();
			productAlbumView.setTParent(null);
		});

		setOnKeyPressed(evt -> {
			if (tabPane.getSelectionModel().getSelectedItem() == productViewTab) {
				switch (evt.getCode()) {
					case A:
						productView.doAddItemDialog();
						break;
					default:
						break;
				}
			}
			if (tabPane.getSelectionModel().getSelectedItem() == artistViewTab) {
				switch (evt.getCode()) {
					case A:
						artistView.doAddItemDialog();
						break;
					default:
						break;
				}
			}
		});

		// Load and display tracked products in the background
		Platform.runLater(() -> {
			Main.settings.trackedProductIds.forEach((id) -> {
				productView.addItemById(id);
			});
		});
		Platform.runLater(() -> {
			Main.settings.trackedArtistIds.forEach((id) -> {
				artistView.addItemById(id, false);
			});
		});

		mainWindow.setOnCloseRequest(evt -> {
			Main.settings.saveToDisk();
		});
	}

	public AnColleTaskManager getTaskManager() {
		return taskManager;
	}

	public Tab newTab(String title, Node content, Object userData) {
		/* Dupe check */
		if (userData != null) {
			for (Tab tab : tabPane.getTabs()) {
				if (tab.getUserData() != null && tab.getUserData().equals(userData)) {
					LOG.log(Level.INFO, "Not adding duplicate tab with userdata: {0}",
							userData.toString());
					flashTab(tab);
					return null;
				}
			}
		}

		Tab tab = createTab(title, content, userData);
		tabPane.getTabs().add(tab);
		return tab;
	}

	private ViewTab createTab(String title, Node content, Object userData) {
		ViewTab tab = new ViewTab(title, content, userData);
		tab.setOnCloseRequest(evt -> handleTabOnCloseRequest(evt));
		return tab;
	}

	/**
	 * Get the tab to the right of the currently selected tab and select it. If
	 * no tab is to the right, this method will have no effect.
	 */
	private void selectTabToRight() {
		int selected = tabPane.getSelectionModel().getSelectedIndex();
		if (tabPane.getTabs().size() > 1
				&& selected + 1 < tabPane.getTabs().size()) {
			setSelectedTab(tabPane.getTabs().get(selected + 1));
		}
	}

	private void flashTab(Tab tab) {
		// TODO: Best-worst hacky animation ever
		Platform.runLater(() -> {
			if (!flashingTabs.contains(tab)) {
				flashingTabs.add(tab);
				Thread animationThread = new Thread(() -> {
					try {
						LOG.log(Level.FINE, "Start flashing tab");
						tab.getStyleClass().add("flashing");
						Thread.sleep(150);
						Platform.runLater(() -> {
							tab.getStyleClass().remove("flashing");
						});
						Thread.sleep(150);
						Platform.runLater(() -> {
							tab.getStyleClass().add("flashing");
						});
						Thread.sleep(150);
						Platform.runLater(() -> {
							LOG.log(Level.FINE, "Finish flashing tab");
							tab.getStyleClass().remove("flashing");
							flashingTabs.remove(tab);
						});
					} catch (InterruptedException ex) {
						LOG.log(Level.SEVERE, null, ex);
					}
				});
				animationThread.start();
			}
		});
	}

	private void handleTabOnCloseRequest(Event evt) {
		if (evt.getTarget() instanceof Tab) {
			Tab closedTab = (Tab) evt.getTarget();
			if (tabPane.getSelectionModel().getSelectedItem() == closedTab) {
				selectTabToRight();
			}
		}
	}

	public void setSelectedTab(Tab tab) {
		tabPane.getSelectionModel().select(tab);
		tab.getContent().requestFocus();
	}

	private boolean closeTab(Tab tab) {
		boolean exists = tabPane.getTabs().contains(tab);
		if (!exists) {
			return false;
		}

		handleTabOnCloseRequest(new Event(tabPane, tab, Tab.CLOSED_EVENT));
		tabPane.getTabs().remove(tab);
		return true;
	}

	public void scrollIntoView(Node node) {
		this.productViewTab.scrollIntoView(node);
	}

	public <T extends Item> void view(T item) {
		if (item instanceof Product) {
			if (productAlbumView.getTParent() != item) {
				productAlbumView.cancelQueuedTasks();
				productAlbumView.setTParent((Product) item);
			}
			if (!tabPane.getTabs().contains(albumViewTab)) {
				tabPane.getTabs().add(2, albumViewTab);
			}
			albumViewTab.setText(item.title_en);
			setSelectedTab(albumViewTab);
		}
		if (item instanceof Artist) {
			if (discogView.getTParent() != item) {
				discogView.cancelQueuedTasks();
				discogView.setTParent((Artist) item);
			}
			if (!tabPane.getTabs().contains(discogViewTab)) {
				tabPane.getTabs().add(2, discogViewTab);
			}
			discogViewTab.setText(item.title_en);
			setSelectedTab(discogViewTab);
		}
	}
}
