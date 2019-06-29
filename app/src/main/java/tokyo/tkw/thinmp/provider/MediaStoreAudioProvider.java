package tokyo.tkw.thinmp.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;

import java.util.ArrayList;

import tokyo.tkw.thinmp.music.Music;
import tokyo.tkw.thinmp.permission.Permission;

public abstract class MediaStoreAudioProvider<T extends Music> {

    protected Context mContext;
    private Permission mPermission;
    protected Cursor mCursor;
    protected Uri uri;
    protected String[] projection;
    protected String selection;
    protected String[] selectionArgs;
    protected String sortOrder;

    abstract T fetch();

    public MediaStoreAudioProvider(Context context) {
        mContext = context;
        mPermission = Permission.createInstance(context);
    }

    public T get() {
        ArrayList<T> list = getList();

        if (list.isEmpty()) return null;

        return list.get(0);
    }

    public ArrayList<T> getList() {

        init();

        ArrayList<T> list = fetchAll();

        destroy();

        return list;
    }

    private ArrayList<T> fetchAll() {
        ArrayList<T> list = new ArrayList<>();

        if (!hasCursor()) return list;

        while (mCursor.moveToNext()) {
            list.add(fetch());
        }

        return list;
    }

    String[] toStringArray(ArrayList<String> list) {
        return list.toArray(new String[list.size()]);
    }

    String makePlaceholders(int size) {
        return TextUtils.join(",",
                IntStream.range(0, size).boxed().map((i) -> "?").collect(Collectors.toList()));
    }

    public void init() {
        if (mPermission.isAllowed()) {
            mCursor = createCursor();
        } else {
            mPermission.requestPermissions();
        }
    }

    private boolean hasCursor() {
        return mCursor != null;
    }

    public Cursor createCursor() {
        return mContext.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private void destroy() {
        if (!hasCursor()) return;

        mCursor.close();
        mCursor = null;
    }
}