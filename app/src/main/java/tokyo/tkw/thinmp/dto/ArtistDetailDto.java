package tokyo.tkw.thinmp.dto;

import com.annimon.stream.Optional;

import java.util.List;

import tokyo.tkw.thinmp.music.Album;
import tokyo.tkw.thinmp.music.Track;

public class ArtistDetailDto {
    public String artistName;
    public String albumsTitle;
    public String songsTitle;
    public List<Album> albumList;
    public List<Track> trackList;
    public Optional<String> albumArtId;
    public int titleSpanSize;
    public int albumListSpanSize;
    public int trackListSpanSize;
}
