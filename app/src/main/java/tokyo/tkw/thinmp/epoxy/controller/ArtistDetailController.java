package tokyo.tkw.thinmp.epoxy.controller;

import android.content.Context;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;
import com.annimon.stream.IntStream;

import java.util.ArrayList;
import java.util.List;

import tokyo.tkw.thinmp.epoxy.model.AlbumListItemModel_;
import tokyo.tkw.thinmp.epoxy.model.HeaderModel_;
import tokyo.tkw.thinmp.epoxy.model.TrackListItemModel_;
import tokyo.tkw.thinmp.listener.AlbumClickListener;
import tokyo.tkw.thinmp.listener.TrackClickListener;
import tokyo.tkw.thinmp.music.Album;
import tokyo.tkw.thinmp.music.Track;

public class ArtistDetailController extends Typed2EpoxyController<ArtistDetailController.Data,
        Context> {
    @AutoModel
    HeaderModel_ headerModel;
    @AutoModel
    AlbumListItemModel_ artistListItemModel;
    @AutoModel
    TrackListItemModel_ trackListItemModel;

    @Override
    protected void buildModels(Data data, Context context) {
        new HeaderModel_()
                .id("albums")
                .title(data.albums)
                .spanSizeOverride((totalSpanCount, position, itemCount) -> data.titleSpanSize)
                .addTo(this);

        for (Album album : data.albumList) {
            new AlbumListItemModel_()
                    .id(album.getId())
                    .albumName(album.getName())
                    .thumbnailId(album.getThumbnailId())
                    .clickListener(new AlbumClickListener(context, album.getId()))
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> data.albumListSpanSize)
                    .addTo(this);
        }

        new HeaderModel_()
                .id("tracks")
                .title(data.songs)
                .spanSizeOverride((totalSpanCount, position, itemCount) -> data.titleSpanSize)
                .addTo(this);

        IntStream.range(0, data.trackList.size()).forEach(i -> {
            Track track = data.trackList.get(i);
            new TrackListItemModel_()
                    .id(track.getId())
                    .trackName(track.getTitle())
                    .thumbnailId(track.getThumbnailId())
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> data.trackListSpanSize)
                    .clickListener(new TrackClickListener(context, data.trackList, i))
                    .addTo(this);
        });
    }

    public static class Data {
        public String albums;
        public List<Album> albumList;
        public String songs;
        public ArrayList<Track> trackList;
        public int titleSpanSize;
        public int albumListSpanSize;
        public int trackListSpanSize;
    }
}