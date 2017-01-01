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
package ancolle.items;

import ancolle.io.IO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javafx.scene.image.Image;

/**
 * Full album details
 *
 * @author samuel
 */
public class Album extends AlbumPreview {

    public final String title_ja_latn; // Romanized name
    public final String pictureUrlSmall;
    private Image picture;
    private final List<Track> tracks;

    public Album(int id, String title_en, String title_ja, String title_ja_latn,
            String type, Date date, String pictureUrlSmall,
            Collection<Track> tracks) {
        super(id, title_en, title_ja, type, date);
        this.title_ja_latn = title_ja_latn;
        this.pictureUrlSmall = pictureUrlSmall;
        this.picture = null; // Load image only when needed
        this.tracks = new ArrayList<>();
        if (tracks != null) {
            tracks.forEach((track) -> {
                this.tracks.add(track);
            });
        }
    }

    /**
     * Get the image associated with this {@link Album} (typically the front
     * album cover).
     *
     * @return the {@link Image} or null if either it failed to be retrieved or
     * does not exist.
     */
    public Image getPicture() {
        if (picture == null) {
            if (pictureUrlSmall == null) {
                // No URL, can't retrive anything
                return null;
            }
            picture = IO.retrievePicture(pictureUrlSmall, "album",
                    id + "_medium");
        }
        return picture;
    }

    /**
     * Return the (sorted) track list as an unmodifiable list
     *
     * @return the track list
     */
    public List<Track> getTracks() {
        Collections.sort(tracks);
        return Collections.unmodifiableList(tracks);
    }

}
