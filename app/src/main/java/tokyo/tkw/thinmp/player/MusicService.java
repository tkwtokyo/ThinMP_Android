package tokyo.tkw.thinmp.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.annimon.stream.Optional;

import java.util.List;

import tokyo.tkw.thinmp.config.PlayerConfig;
import tokyo.tkw.thinmp.register.exists.FavoriteArtistExists;
import tokyo.tkw.thinmp.register.exists.FavoriteSongExists;
import tokyo.tkw.thinmp.track.Track;

public class MusicService extends Service {
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
    private final int PREV_MS = 3000;

    public IBinder binder = new MusicBinder();
    private int repeat;
    private boolean shuffle;
    private Track track;
    private boolean needScreenUpdate;
    private Optional<MediaPlayer> mediaPlayerOptional;
    private PlayingList playingList;
    private OnMusicServiceListener listener;
    private MediaPlayer.OnCompletionListener onCompletionListener;

    @Override
    public void onCreate() {
        super.onCreate();

        PlayerConfig config = PlayerConfig.createInstance(this);

        repeat = config.getRepeat();
        shuffle = config.getShuffle();
        mediaPlayerOptional = Optional.empty();
        needScreenUpdate = false;

        onCompletionListener = createCompletionListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * リスナーを登録
     *
     * @param listener
     */
    public void setListener(OnMusicServiceListener listener) {
        this.listener = listener;
    }

    /**
     * リスナーを削除
     */
    public void unsetListener() {
        listener = null;
    }

    /**
     * listenerがあるか
     * （スリープなど画面を更新する必要がない場合Listenerを削除している）
     */
    private boolean hasListener() {
        return (listener != null);
    }

    /**
     * 再生を開始
     */
    public void initStart(List<Track> playList, int position) {
        playingList = PlayingList.createInstance(playList, position);

        if (shuffle) {
            playingList.shuffle();
        }

        setCurrentTrack();

        start();
    }

    public void start() {
        setMediaPlayer();
        mediaPlayerOptional.ifPresent(mediaPlayer -> {
            mediaPlayer.start();
            onStarted();
            onChangeTrack();
            onScreenUpdate();
        });
    }

    private void setMediaPlayer() {
        destroy();

        mediaPlayerOptional = Optional.ofNullable(MediaPlayer.create(getBaseContext(), track.getUri()));
        mediaPlayerOptional.ifPresentOrElse(
                mediaPlayer -> mediaPlayer.setOnCompletionListener(onCompletionListener),
                this::validation
        );
    }

    private void setCurrentTrack() {
        track = playingList.getTrack();
    }

    private void setPrevTrack() {
        track = playingList.getPrevTrack();
    }

    private void setNextTrack() {
        track = playingList.getNextTrack();
    }

    private void validation() {
        if (playingList.validation()) {
            // 再生する曲があれば次の曲をセットする
            needScreenUpdate = true;
            setCurrentTrack();
            setMediaPlayer();
        } else {
            // 再生する曲がなければ強制終了
            needScreenUpdate = false;
            onForceFinished();
        }
    }

    /**
     * 再生を再開
     */
    public void play() {
        mediaPlayerOptional.ifPresent(MediaPlayer::start);
    }

    /**
     * 一時停止
     */
    public void pause() {
        mediaPlayerOptional.ifPresent(MediaPlayer::pause);
    }

    /**
     * seek
     */
    public void seekTo(int msec) {
        mediaPlayerOptional.ifPresent(mediaPlayer -> mediaPlayer.seekTo(msec));
    }

    /**
     * 現在の再生位置を取得
     *
     * @return ミリ秒
     */
    public int getCurrentPosition() {
        if (mediaPlayerOptional.isEmpty()) return 0;

        return mediaPlayerOptional.get().getCurrentPosition();
    }

    /**
     * リピート
     */
    public int repeat() {
        switch (repeat) {
            case REPEAT_OFF:
                repeatAll();
                break;
            case REPEAT_ALL:
                repeatOne();
                break;
            case REPEAT_ONE:
                repeatOff();
                break;
        }

        // 設定を保存
        PlayerConfig config = PlayerConfig.createInstance(getBaseContext());
        config.setRepeat(repeat);

        return repeat;
    }

    /**
     * 1曲リピート
     */
    private void repeatOne() {
        repeat = REPEAT_ONE;
    }

    /**
     * 全曲リピート
     */
    private void repeatAll() {
        repeat = REPEAT_ALL;
    }

    /**
     * リピートoff
     */
    private void repeatOff() {
        repeat = REPEAT_OFF;
    }

    /**
     * シャッフル
     */
    public boolean shuffle() {
        shuffle = !shuffle;
        if (shuffle) {
            playingList.shuffle();
        } else {
            playingList.undo();
        }

        // 設定を保存
        PlayerConfig config = PlayerConfig.createInstance(getBaseContext());
        config.setShuffle(shuffle);

        return shuffle;
    }

    /**
     * 現在のtrackを取得
     *
     * @return Track
     */
    public Track getTrack() {
        return track;
    }

    /**
     * 前の曲へ
     * 再生時間が3秒を超える場合同じ曲で初期化
     */
    public void prev() {
        if (getCurrentPosition() <= PREV_MS) {
            setPrevTrack();
        }

        setMediaPlayer();
        onChangeTrack();
        onScreenUpdate();
    }

    /**
     * 前の曲へ
     * 再生時間が3秒を超える場合同じ曲を最初から再生
     */
    public void playPrev() {
        if (getCurrentPosition() <= PREV_MS) {
            setPrevTrack();
        }
        start();
    }

    /**
     * 次の曲の再生
     */
    public void playNext() {
        setNextTrack();
        start();
    }

    /**
     * 次の曲へ
     */
    public void next() {
        setNextTrack();
        setMediaPlayer();
        onChangeTrack();
        onScreenUpdate();
    }

    /**
     * 次の曲があるか
     */
    public boolean hasNext() {
        switch (repeat) {
            case REPEAT_OFF:
                return playingList.hasNext();

            case REPEAT_ONE:
                return true;

            case REPEAT_ALL:
                return true;

            default:
                return false;
        }
    }

    /**
     * 再生中か
     *
     * @return
     */
    public boolean isPlaying() {
        if (mediaPlayerOptional.isEmpty()) return false;

        return mediaPlayerOptional.get().isPlaying();
    }

    /**
     * isActive
     *
     * @return
     */
    public boolean isActive() {
        return mediaPlayerOptional.isPresent();
    }

    /**
     * シャッフルの状態を返す
     *
     * @return
     */
    public boolean getShuffle() {
        return shuffle;
    }

    /**
     * リピートの状態を返す
     *
     * @return
     */
    public Integer getRepeat() {
        return repeat;
    }

    /**
     * MediaPlayerを破棄
     */
    public void destroy() {
        mediaPlayerOptional.ifPresent((mediaPlayer) -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        });

        mediaPlayerOptional = Optional.empty();
    }

    public MusicState getState() {
        FavoriteArtistExists favoriteArtistExists = FavoriteArtistExists.createInstance();
        FavoriteSongExists favoriteSongExists = FavoriteSongExists.createInstance();
        Track track = playingList.getTrack();

        return new MusicState(
                mediaPlayerOptional.get().isPlaying(),
                getCurrentPosition(),
                getRepeat(),
                getShuffle(),
                favoriteSongExists.exists(track.getId()),
                favoriteArtistExists.exists(track.getArtistId())
        );
    }

    /**
     * 再生が終わったあとの処理
     */
    private MediaPlayer.OnCompletionListener createCompletionListener() {
        return mp -> {
            onFinished();

            if (!hasNext()) {
                // 再生終了時に最初の曲で画面を更新
                next();
                return;
            }

            if (repeat != REPEAT_ONE) {
                setNextTrack();
            }

            start();
        };
    }

    /**
     * 曲変更
     */
    private void onChangeTrack() {
        if (hasListener()) {
            listener.onChangeTrack(track);
        }
    }

    /**
     * 再生開始
     */
    private void onStarted() {
        if (hasListener()) {
            listener.onStarted();
        }
    }

    /**
     * 再生終了
     */
    private void onFinished() {
        if (hasListener()) {
            listener.onFinished();
        }
    }

    /**
     * 強制終了
     */
    private void onForceFinished() {
        if (hasListener()) {
            track = null;
            mediaPlayerOptional = Optional.empty();
            playingList = null;
            listener.onForceFinished();
        }
    }

    private void onScreenUpdate() {
        if (!needScreenUpdate) return;

        if (hasListener()) {
            listener.onScreenUpdate();
        }

        needScreenUpdate = false;
    }

    /**
     * interface
     */
    public interface OnMusicServiceListener {
        /**
         * 曲変更
         */
        void onChangeTrack(Track track);

        /**
         * 再生開始
         */
        void onStarted();

        /**
         * 再生終了
         */
        void onFinished();

        /**
         * 強制終了
         */
        void onForceFinished();

        /**
         * 画面を更新する必要がある
         */
        void onScreenUpdate();
    }

    /**
     * bind
     */
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
