package tokyo.tkw.thinmp.register.add;

import tokyo.tkw.thinmp.realm.FavoriteSongRealm;
import tokyo.tkw.thinmp.register.RealmRegister;

public class FavoriteSongAdder extends RealmRegister {
    public static FavoriteSongAdder createInstance() {
        return new FavoriteSongAdder();
    }

    public void add(String trackId) {
        beginTransaction();

        realm.createObject(FavoriteSongRealm.class, uuid())
                .set(trackId, increment(FavoriteSongRealm.class, FavoriteSongRealm.ORDER));

        commitTransaction();
    }
}
