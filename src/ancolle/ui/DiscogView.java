package ancolle.ui;

import ancolle.items.AlbumPreview;
import ancolle.items.Artist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiscogView extends AlbumView<Artist> {
	public DiscogView(ApplicationRoot applicationRoot) {
		super(applicationRoot);
	}

	@Override
	protected List<AlbumPreview> getParentAlbums() {
		int totalAlbums = parent.discography.size() + parent.featured_on.size();
		ArrayList<AlbumPreview> allAlbums = new ArrayList<>(totalAlbums);
		allAlbums.addAll(parent.discography);
		allAlbums.addAll(parent.featured_on);
		allAlbums.sort(Comparator.comparing(o -> o.date));
		return allAlbums;
	}
}
