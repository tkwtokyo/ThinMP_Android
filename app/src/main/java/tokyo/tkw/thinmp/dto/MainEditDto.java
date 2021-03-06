package tokyo.tkw.thinmp.dto;

import java.util.HashMap;
import java.util.List;

import tokyo.tkw.thinmp.constant.MainMenuEnum;
import tokyo.tkw.thinmp.shortcut.Shortcut;

public class MainEditDto {
    public String shortcutTitle;
    public String recentlyAddedTitle;
    public List<MainMenuEnum> menuList;
    public List<Shortcut> fromShortcutList;
    public List<Shortcut> shortcutList;
    public int menuStartPosition;
    public int shortcutStartPosition;
    public HashMap<String, Boolean> stateMap;
    public boolean shortcutVisibility;
    public boolean recentlyAddedVisibility;
    public int recentlyAddedCount;
    public int recentlyAddedPosition;
}
