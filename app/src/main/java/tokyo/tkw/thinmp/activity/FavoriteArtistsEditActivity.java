package tokyo.tkw.thinmp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import tokyo.tkw.thinmp.R;
import tokyo.tkw.thinmp.adapter.FavoriteArtistsEditAdapter;
import tokyo.tkw.thinmp.dto.FavoriteArtistDto;
import tokyo.tkw.thinmp.favorite.FavoriteArtistRegister;
import tokyo.tkw.thinmp.logic.FavoriteArtistsEditLogic;
import tokyo.tkw.thinmp.realm.FavoriteArtistRealm;

public class FavoriteArtistsEditActivity extends BaseActivity {
    private FavoriteArtistsEditAdapter adapter;
    private List<FavoriteArtistRealm> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_artist_edit);

        initWithPermissionCheck();
    }

    @Override
    protected void init() {
        // view
        RecyclerView listView = findViewById(R.id.list);
        Button applyView = findViewById(R.id.apply);
        Button cancelView = findViewById(R.id.cancel);

        // logic
        FavoriteArtistsEditLogic logic = FavoriteArtistsEditLogic.createInstance(this);

        // dto
        FavoriteArtistDto dto = logic.createDto();

        // favoriteList
        favoriteList = dto.favoriteList;

        // adapter
        adapter = new FavoriteArtistsEditAdapter(dto.favoriteList, dto.artistMap);
        listView.setAdapter(adapter);

        // layout
        LinearLayoutManager layout = new LinearLayoutManager(this);
        listView.setLayoutManager(layout);

        // ドラッグとスワイプ
        ItemTouchHelper itemTouchHelper = createItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listView);

        // event
        applyView.setOnClickListener(createApplyClickListener());
        cancelView.setOnClickListener(createCancelClickListener());
    }

    private View.OnClickListener createApplyClickListener() {
        return v -> {
            List<String> artistIdList =
                    Stream.of(favoriteList).map(FavoriteArtistRealm::getArtistId).collect(Collectors.toList());
            FavoriteArtistRegister.update(artistIdList);
            finish();
        };
    }

    private View.OnClickListener createCancelClickListener() {
        return v -> {
            finish();
        };
    }

    private ItemTouchHelper createItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
        ) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                // viewの並び替え
                adapter.notifyItemMoved(fromPos, toPos);

                // dataの並び替え
                favoriteList.add(toPos, favoriteList.remove(fromPos));

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int fromPos = viewHolder.getAdapterPosition();

                // 削除
                favoriteList.remove(fromPos);
                adapter.notifyItemRemoved(fromPos);
            }
        });
    }
}