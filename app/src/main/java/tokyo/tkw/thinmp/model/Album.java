package tokyo.tkw.thinmp.model;

import java.util.ArrayList;

/**
 * Created by tk on 2018/03/22.
 */

public class Album {
    private String id;
    private String name;
    private String artistId;
    private String artistName;
    private String thumbnailId;
    private ArrayList<String> trackIdList = new ArrayList<String>();

    public Album(String id, String name, String artistId, String artistName, String thumbnailId) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.artistName = artistName;
        this.thumbnailId = thumbnailId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public ArrayList<String> getTrackIdList() {
        return trackIdList;
    }

    public void addTrackId(String id) {
        trackIdList.add(id);
    }
}
