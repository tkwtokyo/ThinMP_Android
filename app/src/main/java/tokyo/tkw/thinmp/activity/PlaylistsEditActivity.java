package tokyo.tkw.thinmp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;

import io.realm.RealmList;
import tokyo.tkw.thinmp.R;
import tokyo.tkw.thinmp.adapter.PlaylistsEditAdapter;
import tokyo.tkw.thinmp.playlist.Playlist;
import tokyo.tkw.thinmp.playlist.PlaylistRegister;
import tokyo.tkw.thinmp.realm.PlaylistRealm;

public class PlaylistsEditActivity extends AppCompatActivity {
    private PlaylistsEditAdapter mAdapter;
    private RealmList<PlaylistRealm> mList;
    private PlaylistRegister mPlaylistRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists_edit);

        RecyclerView view = findViewById(R.id.list);

        Playlist playlist = new Playlist();
        mList = playlist.getRealmList();
        mAdapter = new PlaylistsEditAdapter(mList);
        mPlaylistRegister = new PlaylistRegister();

        view.setAdapter(mAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        view.setLayoutManager(layout);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(onItemTouchHelperSimpleCallback());
        itemTouchHelper.attachToRecyclerView(view);

        setApply();
        setCancel();

        mPlaylistRegister.beginTransaction();
    }
    private void setApply() {
        findViewById(R.id.apply).setOnClickListener(v -> {
            Stream.of(mList).forEachIndexed((i, realm) -> {
                realm.setOrder(i + 1);
            });
            mPlaylistRegister.commitTransaction();
            finish();
        });
    }

    private void setCancel() {
        findViewById(R.id.cancel).setOnClickListener(v -> {
            mPlaylistRegister.cancelTransaction();
            finish();
        });
    }

    private ItemTouchHelper.SimpleCallback onItemTouchHelperSimpleCallback() {
        return new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
        ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                // viewの並び替え
                mAdapter.notifyItemMoved(fromPos, toPos);
                // dataの並び替え
                mList.add(toPos, mList.remove(fromPos));

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int fromPos = viewHolder.getAdapterPosition();

                mList.get(fromPos).deleteFromRealm();
                mList.remove(fromPos);
                mAdapter.notifyItemRemoved(fromPos);
            }
        };
    }
}