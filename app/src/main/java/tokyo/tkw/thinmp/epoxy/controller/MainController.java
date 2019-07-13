package tokyo.tkw.thinmp.epoxy.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.annimon.stream.Stream;

import java.util.List;

import tokyo.tkw.thinmp.constant.MainMenuEnum;
import tokyo.tkw.thinmp.dto.MainDto;
import tokyo.tkw.thinmp.epoxy.model.ArtistListItemModel_;
import tokyo.tkw.thinmp.epoxy.model.HeaderModel_;
import tokyo.tkw.thinmp.epoxy.model.MainMenuModel_;
import tokyo.tkw.thinmp.epoxy.model.PageHeaderModel_;
import tokyo.tkw.thinmp.epoxy.model.RecentlyAddedModel_;
import tokyo.tkw.thinmp.epoxy.model.SectionHeaderModel_;
import tokyo.tkw.thinmp.epoxy.model.ShortcutAlbumModel_;
import tokyo.tkw.thinmp.epoxy.model.ShortcutArtistModel_;
import tokyo.tkw.thinmp.epoxy.model.ShortcutPlaylistModel_;
import tokyo.tkw.thinmp.listener.AlbumClickListener;
import tokyo.tkw.thinmp.listener.ArtistClickListener;
import tokyo.tkw.thinmp.listener.MainMenuClickListener;
import tokyo.tkw.thinmp.listener.PlaylistClickListener;
import tokyo.tkw.thinmp.music.Album;
import tokyo.tkw.thinmp.realm.ShortcutRealm;
import tokyo.tkw.thinmp.shortcut.Shortcut;

public class MainController extends TypedEpoxyController<MainDto> {
    @AutoModel
    HeaderModel_ headerModel;
    @AutoModel
    ArtistListItemModel_ artistListItemModel;

    @Override
    protected void buildModels(MainDto dto) {
        buildPageHeader(dto.pageTitle, dto.pageTitleSpanSize);
        buildMenu(dto.menuList, dto.mainMenuSpanSize);
        buildShortcutHeader(dto.shortcutTitle, dto.shortcutTitleSpanSize);
        buildShortcut(dto.shortcutList, dto.shortcutListSpanSize);
        buildRecentlyAddedHeader(dto.recentlyAddedTitle, dto.recentlyAddedTitleSpanSize);
        buildRecentlyAdded(dto.recentlyAddedList, dto.recentlyAddedListSpanSize);
    }

    private void buildPageHeader(String title, int spanSize) {
        new PageHeaderModel_()
                .id("header")
                .title(title)
                .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                .addTo(this);
    }

    private void buildMenu(MainMenuEnum[] menuList, int spanSize) {
        Stream.of(menuList).forEachIndexed((i, menu) -> {
            new MainMenuModel_()
                    .id(i)
                    .label(menu.label())
                    .clickListener(new MainMenuClickListener(menu.link()))
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                    .addTo(this);
        });
    }

    private void buildShortcutHeader(String title, int spanSize) {
        new SectionHeaderModel_()
                .id("header")
                .title(title)
                .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                .addTo(this);
    }

    private void buildShortcut(List<Shortcut> shortcutList, int spanSize) {
        Stream.of(shortcutList).forEachIndexed((i, shortcut) -> {
            switch (shortcut.getType()) {
                case ShortcutRealm.ARTIST:
                    new ShortcutArtistModel_()
                            .id(i)
                            .name(shortcut.getName())
                            .type(shortcut.getType())
                            .albumArtId(shortcut.getAlbumArtId())
                            .clickListener(new ArtistClickListener(shortcut.getId()))
                            .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                            .addTo(this);
                    break;
                case ShortcutRealm.ALBUM:
                    new ShortcutAlbumModel_()
                            .id(i)
                            .name(shortcut.getName())
                            .type(shortcut.getType())
                            .albumArtId(shortcut.getAlbumArtId())
                            .clickListener(new AlbumClickListener(shortcut.getId()))
                            .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                            .addTo(this);
                    break;
                case ShortcutRealm.PLAYLIST:
                    new ShortcutPlaylistModel_()
                            .id(i)
                            .name(shortcut.getName())
                            .type(shortcut.getType())
                            .albumArtId(shortcut.getAlbumArtId())
                            .clickListener(new PlaylistClickListener(Integer.parseInt(shortcut.getId())))
                            .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                            .addTo(this);
                    break;
                default:
            }
        });
    }

    private void buildRecentlyAddedHeader(String title, int spanSize) {
        new SectionHeaderModel_()
                .id("header")
                .title(title)
                .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                .addTo(this);
    }

    private void buildRecentlyAdded(List<Album> albumList, int spanSize) {
        Stream.of(albumList).forEachIndexed((i, album) -> {
            new RecentlyAddedModel_()
                    .id(album.getId())
                    .primaryText(album.getName())
                    .secondaryText(album.getArtistName())
                    .albumArtId(album.getAlbumArtId())
                    .clickListener(new AlbumClickListener(album.getId()))
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> spanSize)
                    .addTo(this);
        });
    }
}