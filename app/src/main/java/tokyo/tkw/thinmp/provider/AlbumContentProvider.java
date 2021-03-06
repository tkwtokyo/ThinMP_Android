package tokyo.tkw.thinmp.provider;

import android.content.Context;
import android.provider.MediaStore;

import com.annimon.stream.Optional;

import java.util.List;

import tokyo.tkw.thinmp.album.Album;

public class AlbumContentProvider extends MediaStoreAudioAlbumsProvider {
    public AlbumContentProvider(Context context) {
        super(context);
    }

    public Optional<Album> findById(String albumId) {
        selection = MediaStore.Audio.Albums._ID + " = ?";
        selectionArgs = new String[]{albumId};
        sortOrder = null;

        return get();
    }

    public List<Album> findById(List<String> albumIdList) {
        selection =
                MediaStore.Audio.Albums._ID + " IN (" + makePlaceholders(albumIdList.size()) + ")";
        selectionArgs = toStringArray(albumIdList);
        sortOrder = null;

        return getList();
    }

    public List<Album> findByArtist(String artistId) {
        selection = MediaStore.Audio.Media.ARTIST_ID + " = ?";
        selectionArgs = new String[]{artistId};
        sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";

        return getList();
    }

    public List<Album> findAll() {
        selection = null;
        selectionArgs = null;
        sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";

        return getList();
    }

    public List<Album> findRecentlyAdded(int limit) {
        selection = null;
        selectionArgs = null;
        sortOrder = MediaStore.Audio.Albums._ID + " DESC LIMIT " + limit;

        return getList();
    }
}
