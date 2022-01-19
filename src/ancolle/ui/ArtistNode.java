package ancolle.ui;

import ancolle.items.Artist;
import ancolle.items.ArtistPreview;
import javafx.scene.control.ContextMenu;

public class ArtistNode extends TopLevelItemNode<Artist, ArtistNode, ArtistPreview> {

	private static final ContextMenu CONTEXT_MENU = new TopLevelItemNodeContextMenu();

	public ArtistNode(TopLevelItemView<Artist, ArtistNode, ArtistPreview> view) {
		super(view);
	}

	@Override
	protected ContextMenu getContextMenu() {
		return CONTEXT_MENU;
	}

	@Override
	public void applyAdditionalStyles(Artist item) {

	}
}
