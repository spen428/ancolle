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

/**
 * @author lykat
 */
public class AlbumNode extends ItemNode<AlbumPreview> {

    private static final ContextMenu ALBUM_NODE_CONTEXT_MENU;

    /**
     * The logger for this class.
     */
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

    public AlbumNode(AlbumView albumView) {
	super();
	this.albumView = albumView;
	getStyleClass().add("album-node");

	setOnMouseClicked(evt -> {
	    switch (evt.getButton()) {
		case PRIMARY:
		    if (evt.isControlDown()) {
			openDetails(true);
		    } else {
			toggleCollected();
		    }
		    break;
		case MIDDLE:
		    openDetails();
		    break;
		case SECONDARY:
		    showContextMenu(evt);
		    break;
		default:
		    break;
	    }
	});
    }

    /**
     * Set the `collected` status of this {@link AlbumNode}.
     *
     * @param collected the value to set
     */
    public void setCollected(boolean collected) {
	this.collected = collected;
	if (collected) {
	    getStyleClass().add("collected");
	} else {
	    getStyleClass().remove("collected");
	}
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
	boolean contains = albumView.ancolle.getSettings().collectedAlbumIds.contains(album.id);
	if (!contains) {
	    albumView.ancolle.getSettings().collectedAlbumIds.add(album.id);
	} else {
	    albumView.ancolle.getSettings().collectedAlbumIds.remove(album.id);
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
	boolean status = albumView.ancolle.getSettings().hiddenAlbumIds.contains(album.id);
	if (!status) {
	    albumView.ancolle.getSettings().hiddenAlbumIds.add(album.id);
	} else {
	    albumView.ancolle.getSettings().hiddenAlbumIds.remove(album.id);
	}
	status = !status;
	setHidden(status);
	albumView.updateHiddenItems();
    }

    private void openDetails() {
	openDetails(false);
    }

    private void openDetails(boolean selectTab) {
	// If full album details are loaded, open details in a new tab
	Album fullAlbum = albumView.fullAlbumMap.get(getAlbum());
	if (fullAlbum != null) {
	    AlbumDetailsView adv = new AlbumDetailsView(fullAlbum);
	    Tab tab = albumView.ancolle.newTab(fullAlbum.title_ja,
		    adv, "adv_" + fullAlbum.id);
	    if (tab != null) {
		tab.getStyleClass().add("album-details-tab");
		if (selectTab) {
		    albumView.ancolle.setSelectedTab(tab);
		}
	    }
	}
    }

}
