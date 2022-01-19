package ancolle.ui;

import ancolle.items.Artist;
import ancolle.items.ArtistPreview;
import ancolle.main.Main;

import java.util.List;
import java.util.logging.Logger;

public final class ArtistView extends TopLevelItemView<Artist, ArtistNode, ArtistPreview> {

	private static final Logger LOG = Logger.getLogger(ArtistView.class.getName());

	public ArtistView(ApplicationRoot ancolle) {
		super(ancolle);
		getStyleClass().add("artist-view");
	}

	@Override
	protected String getItemTypeName() {
		return "artist";
	}

	@Override
	protected ArtistNode instantiateTNode() {
		return new ArtistNode(this);
	}

	@Override
	protected List<Integer> getTrackedTopLevelItemSettings() {
		return Main.settings.trackedArtistIds;
	}

	@Override
	protected Class<ArtistNode> getTNodeClass() {
		return ArtistNode.class;
	}

	@Override
	public void refreshItems() {

	}
}
