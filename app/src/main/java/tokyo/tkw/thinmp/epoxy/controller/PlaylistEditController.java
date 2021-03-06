package tokyo.tkw.thinmp.epoxy.controller;

import com.airbnb.epoxy.TypedEpoxyController;
import com.annimon.stream.Stream;

import java.util.List;

import tokyo.tkw.thinmp.dto.PlaylistsEditDto;
import tokyo.tkw.thinmp.epoxy.model.PlaylistEditModel_;
import tokyo.tkw.thinmp.playlist.Playlist;

public class PlaylistEditController extends TypedEpoxyController<PlaylistsEditDto> {

    @Override
    protected void buildModels(PlaylistsEditDto dto) {
        buildList(dto.playlists);
    }

    private void buildList(List<Playlist> playlists) {
        Stream.of(playlists).forEachIndexed((i, playlist) -> {
            new PlaylistEditModel_()
                    .id(playlist.getId())
                    .albumArtId(playlist.getAlbumArtId())
                    .primaryText(playlist.getName())
                    .addTo(this);
        });
    }
}
