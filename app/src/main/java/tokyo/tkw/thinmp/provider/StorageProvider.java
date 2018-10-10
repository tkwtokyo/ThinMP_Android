package tokyo.tkw.thinmp.provider;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * StorageProvider
 */
public class StorageProvider {
    private final int PERMISSION_CODE = 1;
    private Activity mContext;

    public StorageProvider(Activity context) {
        mContext = context;
    }

    public Cursor getCursor() {
        if (isAllowed()) {
            return mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media.IS_MUSIC + " = 1",
                    null,
                    null
            );
        } else {
            ActivityCompat.requestPermissions( mContext,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_CODE);
            return null;
        }
    }

    /**
     * ストレージにアクセスする権限があるか
     *
     * @return
     */
    private boolean isAllowed() {
        int permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);

        return permission == PackageManager.PERMISSION_GRANTED;
    }
}