package tokyo.tkw.thinmp.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;

import tokyo.tkw.thinmp.music.PlayingList;
import tokyo.tkw.thinmp.music.Track;
import tokyo.tkw.thinmp.util.ActivityUtil;

public class MusicService extends Service {
    private final int REPEAT_OFF = 0;
    private final int REPEAT_ONE = 1;
    private final int REPEAT_ALL = 2;

    private int mRepeat = REPEAT_OFF;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Track> mOriginalList;
    private PlayingList mPlayingList;
    private OnMusicServiceListener mListener;

    public IBinder mBinder = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * bind
     */
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    /**
     * 再生リストを登録
     * @param playList
     * @param position
     */
    public void setPlayingList(ArrayList<Track> playList, int position) {
        mOriginalList = playList;
        mPlayingList = new PlayingList(playList, position);
    }

    /**
     * リスナーを登録
     * @param listener
     */
    public void setListener(OnMusicServiceListener listener) {
        mListener = listener;
    }

    /**
     * 再生を開始
     */
    public void start() {
        destroy();

        Track track = mPlayingList.getTrack();
        mMediaPlayer = MediaPlayer.create((Context) ActivityUtil.getContext(), track.getUri());
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(onCompletionListener);

        mListener.onChangeTrack(track);
    }

    /**
     * 再生を再開
     */
    public void play() {
        mMediaPlayer.start();
    }

    /**
     * 一時停止
     */
    public void pause() {
        mMediaPlayer.pause();
    }

    /**
     * seek
     */
    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    /**
     * 現在の再生位置を取得
     * @return ミリ秒
     */
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 1曲リピート
     */
    public void setRepeatOne() {
        mRepeat = REPEAT_ONE;
    }

    /**
     * 全曲リピート
     */
    public void setRepeatAll() {
        mRepeat = REPEAT_ALL;
    }

    /**
     * リピートoff
     */
    public void setRepeatOff() {
        mRepeat = REPEAT_OFF;
    }

    /**
     * シャッフルon
     */
    public void setShuffleOn() {
        mPlayingList = new PlayingList(mOriginalList, mPlayingList.getCurrentPosition());
    }

    /**
     * シャッフルoff
     */
    public void setShuffleOff() {
        mPlayingList = mPlayingList.getShufflePlayingList();
    }

    /**
     * 現在のtrackを取得
     * @return Track
     */
    public Track getTrack() {
        if (mPlayingList == null) return null;

        return mPlayingList.getTrack();
    }

    /**
     * 前の曲へ
     */
    public void prev() {
        mPlayingList.prev();
    }

    /**
     * 次の曲へ
     */
    public void next() {
        mPlayingList.next();
    }

    /**
     * 再生中か
     * @return
     */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * MediaPlayerを破棄
     */
    public void destroy() {
        if (mMediaPlayer == null) return;

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    /**
     * 再生が終わったあとの処理
     */
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }
    };

    /**
     * 再生が終わったあとに次の曲の再生
     */
    private void playNext() {
        switch (mRepeat) {
            case REPEAT_OFF :
                if (mPlayingList.hasNext()) {
                    mPlayingList.next();
                    start();
                }

                return;
            case REPEAT_ONE :
                // MediaPlayerの機能でリピートするので何もしない

                return;
            case REPEAT_ALL :
                mPlayingList.next();
                start();

                return;
            default:
                return;
        }
    }

    /**
     * interface
     */
    public interface OnMusicServiceListener {
        /**
         * 曲変更
         */
        void onChangeTrack(Track track);
    }
}
