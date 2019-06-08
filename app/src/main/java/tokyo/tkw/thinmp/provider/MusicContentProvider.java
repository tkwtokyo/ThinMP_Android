package tokyo.tkw.thinmp.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import tokyo.tkw.thinmp.music.Music;

import static android.net.Uri.parse;

public abstract class MusicContentProvider<T extends Music> {
    private final int PERMISSION_CODE = 1;
    protected Context mContext;
    private Cursor mCursor;

    public MusicContentProvider(Context context) {
        mContext = context;
    }

    public ArrayList<T> getList() {
        init();

        ArrayList<T> list = fetchAll();

        destroy();

        return list;
    }

    abstract Cursor createCursor();

    abstract T fetch();

    protected String getId() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID));
    }

    protected String getTitle() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
    }

    protected String getArtistId() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
    }

    protected String getArtistName() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
    }

    protected Uri getUri() {
        return parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + getId());
    }

    protected String getAlbumId() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
    }

    protected String getAlbumName() {
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
    }

    protected int getDuration() {
        return mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
    }

    private void init() {
        if (isAllowed()) {
            mCursor = createCursor();
        } else {
            requestPermissions();
        }
    }

    private boolean hasCursor() {
        return mCursor != null;
    }

    private ArrayList<T> fetchAll() {
        ArrayList<T> list = new ArrayList<>();

        if (!hasCursor()) return list;

        while (mCursor.moveToNext()) {
            list.add(fetch());
        }

        return list;
    }

    private boolean isAllowed() {
        int permission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) mContext,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    private void destroy() {
        if (!hasCursor()) return;

        mCursor.close();
        mCursor = null;
    }
}