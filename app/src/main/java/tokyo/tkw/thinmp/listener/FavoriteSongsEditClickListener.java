package tokyo.tkw.thinmp.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import tokyo.tkw.thinmp.activity.FavoriteSongsEditActivity;

public class FavoriteSongsEditClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, FavoriteSongsEditActivity.class);
        context.startActivity(intent);
    }
}
