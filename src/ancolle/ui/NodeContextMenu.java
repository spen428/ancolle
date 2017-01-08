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

import java.util.logging.Logger;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.stage.Window;

/**
 * A {@link ContextMenu} to be displayed when a {@link Node} is right-clicked.
 *
 * @author lykat
 * @param <T> the type of {@link Node} that will be used to anchor this
 * {@link NodeContextMenu} when it is displayed
 */
public class NodeContextMenu<T extends Node> extends ContextMenu {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(NodeContextMenu.class.getName());

    public NodeContextMenu() {
	super();
    }

    @SuppressWarnings("unchecked")
    public T getNode() {
	return (T) getOwnerNode();
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
	super.show(anchor, screenX, screenY); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void show(Node anchor, Side side, double dx, double dy) {
	super.show(anchor, side, dx, dy); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@link NodeContextMenu} may not use this method. This will throw an
     * {@link IllegalAccessError} if called.
     */
    @Override
    @Deprecated
    public void show(Window ownerWindow, double anchorX, double anchorY) {
	throw new IllegalAccessError(NodeContextMenu.class.getName()
		+ " context menus may only use the show(Node anchor, ...) "
		+ "methods.");
    }

    /**
     * {@link NodeContextMenu} may not use this method. This will throw an
     * {@link IllegalAccessError} if called.
     */
    @Override
    @Deprecated
    public void show(Window owner) {
	throw new IllegalAccessError(NodeContextMenu.class.getName()
		+ " context menus may only use the show(Node anchor, ...) "
		+ "methods.");
    }

    /**
     * {@link NodeContextMenu} may not use this method. This will throw an
     * {@link IllegalAccessError} if called.
     */
    @Override
    @Deprecated
    protected void show() {
	throw new IllegalAccessError(NodeContextMenu.class.getName()
		+ " context menus may only use the show(Node anchor, ...) "
		+ "methods.");
    }

}