package tokyo.tkw.thinmp.artist;

import android.content.Context;

import com.annimon.stream.Optional;

import java.util.List;

import tokyo.tkw.thinmp.album.Album;
import tokyo.tkw.thinmp.music.Music;
import tokyo.tkw.thinmp.provider.ArtistContentProvider;
import tokyo.tkw.thinmp.track.Track;

public class Artist extends Music {
    public static final String ARTIST_ID = "artistId";
    private static final String META_FORMAT = "%s albums, %s songs";

    private Context context;
    private String numberOfAlbums;
    private String numberOfTracks;
    private String albumArtId;

    public Artist(Context context, String id, String name, String numberOfAlbums, String numberOfTracks) {
        this.context = context;
        this.id = id;
        this.name = name;
        this.numberOfAlbums = numberOfAlbums;
        this.numberOfTracks = numberOfTracks;
    }

    public static Optional<Artist> createInstance(Context context, String id) {
        ArtistContentProvider provider = new ArtistContentProvider(context);
        Optional<Artist> artistOptional = provider.findById(id);

        return artistOptional.map(artist -> {
            artist.setAlbumArtId(artist.findAlbumArtId());
            return artist;
        });
    }

    public String getAlbumArtId() {
        return albumArtId;
    }

    public void setAlbumArtId(String albumArtId) {
        this.albumArtId = albumArtId;
    }

    public List<Album> getAlbumList() {
        ArtistAlbum artistAlbum = ArtistAlbum.createInstance(context, id);

        return artistAlbum.getAlbumList();
    }

    public List<Track> getTrackList() {
        ArtistTrack artistTrack = ArtistTrack.createInstance(context, id);

        return artistTrack.getTrackList();
    }

    public String getMeta() {
        return String.format(META_FORMAT, numberOfAlbums, numberOfTracks);
    }

    private String findAlbumArtId() {
        ArtistAlbumArt artistAlbumArt = ArtistAlbumArt.createInstance(context, id);

        return artistAlbumArt.getAlbumArtId();
    }
}
