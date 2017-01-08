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

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * Extends {@link Tab} to add the pseudo-class "flashing"
 *
 * @author lykat
 */
public class MyTab extends Tab {

    private final PseudoClass pseudoClassFlashing;

    public MyTab(String title, Node content) {
	this(title, content, null);
    }

    public MyTab(String title, Node content, Object userData) {
	super(title, content);
	setUserData(userData);
	this.pseudoClassFlashing = new PseudoClass() {
	    @Override
	    public String getPseudoClassName() {
		return "flashing";
	    }
	};
    }

}
