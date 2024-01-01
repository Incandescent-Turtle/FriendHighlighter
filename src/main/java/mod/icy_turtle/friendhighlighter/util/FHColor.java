package mod.icy_turtle.friendhighlighter.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom Color enum for this mod with common colours.
 */
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

    /**
     *  A map holding all mod colors, with the lower case color name as the key and the 6 digit hex code as the value (#XXXXXX).
     */
    public static final Map<String, String> COLOR_MAP = new HashMap<>();

    static
    {
        //  putting all the enum colors into the map
        for (FHColor c : FHColor.values())
        {
            COLOR_MAP.put(c.name().toLowerCase(), c.code);
        }
    }

    /**
     * The hex code for this color.
     */
    public final String code;

    FHColor(String code)
    {
        this.code = code;
    }

    /**
     * Gets the hex string for the given plaintext color name
     * @param colorName not case-sensitive,name of the color. "green", "yellow", etc.
     * @return returns the hex code if the color is in the map, or null if it isn't.
     */
    public static @Nullable String getHexFromColorName(String colorName)
    {
        return COLOR_MAP.get(colorName.toLowerCase());
    }
}