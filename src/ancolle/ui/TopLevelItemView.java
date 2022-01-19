package ancolle.ui;

import ancolle.io.VgmdbApi;
import ancolle.items.Item;
import ancolle.items.ItemWithPicture;
import ancolle.main.Main;
import ancolle.ui.concurrency.AnColleTask;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TopLevelItemView<T extends Item, TNode extends ItemNode<T>, TPreview extends Item>
		extends TilePaneView {

	private static final Logger LOG = Logger.getLogger(TopLevelItemView.class.getName());

	private final Set<T> items;

	public TopLevelItemView(ApplicationRoot applicationRoot) {
		super(applicationRoot);
		getStyleClass().add(getItemTypeName() + "-view");
		this.items = new HashSet<>(32);

		// Button for adding new items to track
		getChildren().add(new TopLevelAdderNode(this, getItemTypeName()));
	}

	protected abstract String getItemTypeName();

	public void doAddItemDialog() {
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.initOwner(ancolle.mainWindow);
		inputDialog.initModality(Modality.WINDOW_MODAL);
		inputDialog.setTitle("Track a new " + getItemTypeName());
		inputDialog.setContentText("Enter a " + getItemTypeName() + " name:");
		centreDialog(inputDialog);

		TPreview chosenItem = null;
		Optional<String> result = inputDialog.showAndWait();
		if (result.isPresent()) {
			// Display a "loading" dialog while the search completes
			Alert loadingDialog = createUnclosableAlert();
			loadingDialog.initOwner(ancolle.mainWindow);
			loadingDialog.initModality(Modality.WINDOW_MODAL);
			loadingDialog.setTitle("Searching...");
			loadingDialog.setHeaderText("Search in progress...");
			loadingDialog.show();

			String text = result.get();
			List<TPreview> searchResults = VgmdbApi.searchFor(getItemTypeName(), text);
			// Finished loading now, so close the dialog and continue
			loadingDialog.close();

			if (searchResults.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.initOwner(ancolle.mainWindow);
				alert.initModality(Modality.WINDOW_MODAL);
				alert.setTitle("No search results");
				alert.setContentText("No " + getItemTypeName() + "s could be found that "
						+ "matched the search terms: \"" + text + "\"");
				centreDialog(alert);

				alert.showAndWait();
			} else {
				// Build list of choices
				List<String> choices = new ArrayList<>(searchResults.size());
				for (TPreview searchResult : searchResults) {
					choices.add(searchResult.toString());
				}

				// Show choice dialog
				ChoiceDialog<String> resultsChooser = new ChoiceDialog<>();
				resultsChooser.getItems().addAll(choices);
				resultsChooser.setSelectedItem(choices.get(getDefaultChoice(searchResults)));
				resultsChooser.initOwner(ancolle.mainWindow);
				resultsChooser.initModality(Modality.WINDOW_MODAL);
				resultsChooser.setTitle("Select a " + getItemTypeName());
				resultsChooser.setContentText("Select the " + getItemTypeName() + " from the list"
						+ " that you wish to add to the " + getItemTypeName() + " tracker.");
				centreDialog(resultsChooser);

				Optional<String> choice = resultsChooser.showAndWait();
				if (choice.isPresent()) {
					String c = choice.get();
					chosenItem = searchResults.get(choices.indexOf(c));
				}
			}
		}

		if (chosenItem != null) {
			final TPreview p = chosenItem;
			// Add to tracked list
			Platform.runLater(() -> {
				getTrackedTopLevelItemSettings().add(p.id);
				addItemById(p.id, true);
				Main.settings.saveToDisk();
			});
		}
	}

	protected int getDefaultChoice(List<TPreview> searchResults) {
		return 0;
	}

	private void centreDialog(Dialog<?> dialog) {
		Scene scene = getScene();
		double x = scene.getX() + scene.getWidth() / 2;
		double y = scene.getY() + scene.getHeight() / 2;
		x -= dialog.getWidth() / 2;
		y -= dialog.getHeight() / 2;
		dialog.setX(x);
		dialog.setY(y);
	}

	public void updateHiddenItems() {
		// TODO
	}

	private Alert createUnclosableAlert() {
		// TODO: Prevent Alt + F4 close event - consuming the
		// WINDOW_CLOSING_EVENT doesn't seem to work...
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		DialogPane dialogPane = alert.getDialogPane();
		/*
		 * The INFORMATION dialog contains three children by default: a
		 * GridPane, a Label, and a ButtonBar. The ButtonBar will be removed so
		 * the user cannot click anything to close the dialog, and the Label
		 * will be removed for aesthetic reasons, as the content text is not set.
		 */
		dialogPane.getChildren().removeIf(child -> (child instanceof ButtonBar
				|| child instanceof Label));
		// Set the style to UNDECORATED to remove the close button at the top.
		// alert.initStyle(StageStyle.UNDECORATED);
		// TODO: Setting the style to undecorated seems to prevent the dialog
		// from appearing altogether...
		return alert;
	}

	static class TopLevelAdderNode extends ItemNode<Object> {

		protected TopLevelAdderNode(TopLevelItemView view, String itemTypeName) {
			getStyleClass().add(itemTypeName + "-adder-node");
			getChildren().remove(imageViewContainer);
			label1.setText("+");
			label2.setText("Click here to add a(n) " + itemTypeName + " to the tracker.");

			setOnMouseClicked(evt -> {
				if (evt.getButton() == MouseButton.PRIMARY) {
					view.doAddItemDialog();
				}
			});
		}

		@Override
		protected ContextMenu getContextMenu() {
			return null;
		}

		@Override
		public void applyAdditionalStyles(Object item) {
		}
	}

	public boolean addItem(T item, boolean highlight) {
		return addItem(item, -1, highlight);
	}

	public boolean addItem(T item, int idx, boolean highlight) {
		if (this.items.contains(item)) {
			return false;
		}
		this.items.add(item);
		TNode node = createItemNode(item.title_en, item.title_ja);
		node.setItem(item);

		// Styling
		node.applyAdditionalStyles(item);

		// Get logo
		retrievePictureInBackground(item, node);

		// Insert into view
		if (idx != -1) {
			// Insert with provided idx value
			getChildren().add(idx, node);
		} else {
			// Insert sorted
			insertT(node);
		}

		// Highlight as new
		if (highlight) {
			node.getStyleClass().add("new");
			ancolle.scrollIntoView(node);
			node.requestFocus();
		}
		return true;
	}

	private void retrievePictureInBackground(T item, TNode node) {
		if (!(item instanceof ItemWithPicture)) {
			return;
		}

		submitBackgroundTask(new AnColleTask(() -> {
			LOG.log(Level.FINE, "Fetching " + getItemTypeName() + " cover for " + getItemTypeName() + " #", item.id);
			final Image image = ((ItemWithPicture) item).getPicture();
			LOG.log(Level.FINE, "Fetched " + getItemTypeName() + " cover for " + getItemTypeName() + " #", item.id);
			Platform.runLater(() -> {
				node.imageViewContainer.setImage(image);
			});
		}, 1, this, getItemTypeName() + "_" + item.id + "_picture"));
	}

	private TNode createItemNode(String label1text, String label2text) {
		TNode node = instantiateTNode();
		node.label1.setText(label1text);
		node.label2.setText(label2text);
		return node;
	}

	protected abstract TNode instantiateTNode();

	public void addItemById(int id) {
		addItemById(id, false);
	}

	public void addItemById(int id, boolean highlight) {
		final Node placeholder = createItemNode(getItemTypeName() + " #" + id, "Loading...");
		// Insert before "Add"  button
		getChildren().add(getChildren().size() - 1, placeholder);
		submitBackgroundTask(new AnColleTask(() -> {
			final T item = VgmdbApi.getById(getItemTypeName(), id);
			if (item != null) {
				Platform.runLater(() -> {
					boolean added = addItem(item, highlight);
					if (!added) {
						LOG.log(Level.INFO, getItemTypeName() + " with id #{0} was not "
								+ "added. Possible duplicate?", item.id);
					}
					getChildren().remove(placeholder);
				});
			}
		}, 0, this, getItemTypeName() + "_" + id));
	}

	boolean removeItem(T item) {
		if (!this.items.contains(item)) {
			return false;
		}

		// Be careful to use Integer object here, otherwise the remove-by-idx method will be called
		getTrackedTopLevelItemSettings().remove((Integer) item.id);
		Main.settings.saveToDisk();

		this.items.remove(item);
		// Find and remove the node
		boolean removed = getChildren()
				.removeIf(child -> (getTNodeClass().isInstance(child))
						&& ((TNode) child).getItem() == item);

		return removed;
	}

	protected abstract List<Integer> getTrackedTopLevelItemSettings();

	protected abstract Class<TNode> getTNodeClass();

	private void insertT(TNode node) {
		int insertIdx;
		for (insertIdx = 0; insertIdx < getChildren().size(); insertIdx++) {
			Node child = getChildren().get(insertIdx);
			if (ItemNodeComparators.PRODUCT_NODE_COMPARATOR.compare(node, child) <= 0) {
				break;
			}
		}
		getChildren().add(insertIdx, node);
	}
}
