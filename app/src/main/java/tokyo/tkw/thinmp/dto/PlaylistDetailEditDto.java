package tokyo.tkw.thinmp.dto;

import java.util.List;

import tokyo.tkw.thinmp.track.Track;

public class PlaylistDetailEditDto {
    public StringBuffer playlistName;
    public List<Track> trackList;
    public List<String> trackIdList;
    public int startPosition;
}
