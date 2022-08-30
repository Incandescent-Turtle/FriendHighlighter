package mod.icy_turtle.friendhighlighter.util;

import java.util.HashMap;
import java.util.Map;

public enum FHColor
{
    RED("#e6194B"),
    GREEN("#3cb44b"),
    YELLOW("#ffe119"),
    BLUE("#0000FF"),
    ORANGE("#f58231"),
    PURPLE("#911eb4"),
    CYAN("#42d4f4"),
    MAGENTA("#f032e6"),
    LIME("#bfef45"),
    PINK("#fabed4"),
    LAVENDER("#dcbeff"),
    BROWN("#9A6324"),
    MINT("#aaffc3"),
    OLIVE("#808000"),
    APRICOT("#ffd8b1"),
    NAVY("#000075"),
    GREY("#a9a9a9"),
    WHITE("#ffffff"),
    BLACK("#000000");

    public static final Map<String, String> COLOR_MAP = new HashMap<>();

    static
    {
        for (FHColor c : FHColor.values())
        {
            COLOR_MAP.put(c.name().toLowerCase(), c.code);
        }
    }

    final String code;

    FHColor(String code)
    {
        this.code = code;
    }

    public static String getHex(String colorName)
    {
        return COLOR_MAP.get(colorName.toLowerCase());
    }
}