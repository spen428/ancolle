package ancolle.ui;

import javafx.css.PseudoClass;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * @param <T> the type of {@link ancolle.items.Item} that this node represents
 * @author lykat
 */
public abstract class ItemNode<T> extends VBox {

	public final ImageViewContainer imageViewContainer;
	public final Label label1;
	public final Label label2;

	private T item;

	private boolean hidden = false;

	private final PseudoClass pseudoClassHover;

	public ItemNode() {
		getStyleClass().add("item-node");
		pseudoClassHover = new PseudoClass() {
			@Override
			public String getPseudoClassName() {
				return "hover";
			}
		};

		imageViewContainer = new ImageViewContainer();
		getChildren().add(imageViewContainer);

		label1 = new Label();
		label1.getStyleClass().add("label1");
		getChildren().add(label1);

		label2 = new Label();
		label2.getStyleClass().add("label2");
		getChildren().add(label2);

		// Mouse hover highlighting
		setOnMouseEntered(evt -> {
			pseudoClassStateChanged(getPseudoClassHover(), true);
			ContextMenu cm = getContextMenu();
			if (cm != null) {
				cm.hide();
			}
		});
		setOnMouseExited(evt -> {
			pseudoClassStateChanged(getPseudoClassHover(), false);
		});
	}

	abstract protected ContextMenu getContextMenu();

	/**
	 * Open the context menu, anchored to this {@link ItemNode} and offset
	 * according to the location of the mouse click that triggered this event
	 *
	 * @param evt the {@link MouseEvent} that triggered the opening of the
	 *            context menu
	 */
	protected void showContextMenu(MouseEvent evt) {
		double dx = evt.getX();
		double dy = evt.getY();
		getContextMenu().show(this, Side.TOP, dx, dy);
	}

	/**
	 * @return the item
	 */
	protected T getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	protected void setItem(T item) {
		this.item = item;
	}

	/**
	 * @return the pseudoClassHover
	 */
	public PseudoClass getPseudoClassHover() {
		return pseudoClassHover;
	}

	/**
	 * Set the `hidden` status of this {@link ItemNode}.
	 *
	 * @param hidden the value to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		if (hidden) {
			getStyleClass().add("hidden");
		} else {
			getStyleClass().remove("hidden");
		}
	}

	public boolean isHidden() {
		return hidden;
	}

	public abstract void applyAdditionalStyles(T item);
}
