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

import ancolle.items.Album;
import ancolle.items.AlbumPreview;
import java.util.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * @author lykat
 */
public class AlbumNode extends ItemNode<AlbumPreview> {

    public static final Background COLOR_NOT_COLLECTED = new Background(new BackgroundFill(null, null, null));
    public static final Background COLOR_COLLECTED = new Background(new BackgroundFill(Color.FORESTGREEN, null, null));
    public static final double HIDDEN_OPACITY = 0.4;

    private static final ContextMenu ALBUM_NODE_CONTEXT_MENU;
    private static final Logger LOG = Logger.getLogger(AlbumNode.class.getName());

    static {
	ALBUM_NODE_CONTEXT_MENU = new ContextMenu();
	MenuItem menuItemHide = new MenuItem("Hide Album");
	menuItemHide.setOnAction(evt -> {
	    AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
	    node.toggleHidden();
	});
	ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemHide);

	MenuItem menuItemReload = new MenuItem("Reload Album");
	menuItemReload.setOnAction(evt -> {
	    AlbumNode node = (AlbumNode) ALBUM_NODE_CONTEXT_MENU.getOwnerNode();
	    node.reloadAlbum();
	});
//	ALBUM_NODE_CONTEXT_MENU.getItems().add(menuItemReload);

	ALBUM_NODE_CONTEXT_MENU.getItems().add(new MenuItem("Cancel"));
	ALBUM_NODE_CONTEXT_MENU.setAutoHide(true);
    }

    private final AlbumView albumView;

    private boolean collected = false;
    private boolean hidden = false;

    public AlbumNode(double maxWidth, AlbumView albumView) {
	super();
	this.albumView = albumView;

	setMaxWidth(maxWidth);

	setOnMouseExited(evt -> {
	    updateBackground();
	});
	setOnMouseClicked(evt -> {
	    switch (evt.getButton()) {
		case PRIMARY:
		    toggleCollected();
		    break;
		case MIDDLE:
		    // If full album details are loaded, open details in a new tab
		    Album fullAlbum = albumView.fullAlbumMap.get(getAlbum());
		    if (fullAlbum != null) {
			AlbumDetailsView adv = new AlbumDetailsView(fullAlbum);
			Tab tab = albumView.ancolle.newTab(fullAlbum.title_ja, adv);
			// albumView.ancolle.setSelectedTab(tab);
		    }
		    break;
		case SECONDARY:
		    showContextMenu(evt);
		    break;
		default:
		    break;
	    }
	});
    }

    public boolean isHidden() {
	return hidden;
    }

    private AlbumView getAlbumView() {
	return albumView;
    }

    /**
     * Update the background colour of this {@link AlbumNode}, setting it to be
     * indicative of its `collected` status.
     */
    public void updateBackground() {
	setBackground(collected ? COLOR_COLLECTED : COLOR_NOT_COLLECTED);
    }

    /**
     * Set the `collected` status of this {@link AlbumNode}. This will
     * automatically call {@link AlbumNode#updateBackground()}
     *
     * @param collected the value to set
     */
    public void setCollected(boolean collected) {
	this.collected = collected;
	updateBackground();
    }

    /**
     * Whether this {@link AlbumNode} has been marked as "collected"
     *
     * @return the `collected` status
     */
    public boolean isCollected() {
	return this.collected;
    }

    @Override
    protected ContextMenu getContextMenu() {
	return ALBUM_NODE_CONTEXT_MENU;
    }

    private void toggleCollected() {
	AlbumPreview album = getAlbum();
	if (album == null) {
	    return;
	}
	boolean contains = albumView.ancolle.settings.collectedAlbumIds.contains(album.id);
	if (!contains) {
	    albumView.ancolle.settings.collectedAlbumIds.add(album.id);
	} else {
	    albumView.ancolle.settings.collectedAlbumIds.remove(album.id);
	}
	contains = !contains;
	setCollected(contains);
    }

    public AlbumPreview getAlbum() {
	return getItem();
    }

    public void setAlbum(AlbumPreview album) {
	setItem(album);
    }

    private void reloadAlbum() {
	AlbumPreview album = getAlbum();
	if (album == null) {
	    return;
	}
	// TODO
	// albumView.fullAlbumMap.remove(album);
    }

    private void toggleHidden() {
	AlbumPreview album = getAlbum();
	if (album == null) {
	    return;
	}
	boolean status = albumView.ancolle.settings.hiddenAlbumIds.contains(album.id);
	if (!status) {
	    albumView.ancolle.settings.hiddenAlbumIds.add(album.id);
	} else {
	    albumView.ancolle.settings.hiddenAlbumIds.remove(album.id);
	}
	status = !status;
	setHidden(status);
	albumView.updateHiddenItems();
    }

    /**
     * Set the `hidden` status of this {@link AlbumNode}. This will
     * automatically update the parent {@link AlbumView}
     *
     * @param hidden the value to set
     */
    public void setHidden(boolean hidden) {
	this.hidden = hidden;
	setOpacity(hidden ? HIDDEN_OPACITY : 1.0);
    }

}
