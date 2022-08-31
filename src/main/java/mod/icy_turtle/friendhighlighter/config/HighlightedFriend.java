package mod.icy_turtle.friendhighlighter.config;

/**
 * Represents an entity that can be highlighted through this mod.
 */
public class HighlightedFriend
{
    public String name;
    public int color;

    /**
     * Whether this only represents players, instead of all entities.
     */
    public boolean onlyPlayers;

    /**
     * Whether this friend should be highlighted, or just have their name tag show and be colored.
     */
    public boolean outlineFriend;

    /**
     * Creates a default friend with initialized values.
     * @see #HighlightedFriend(String, int)
     * @see #HighlightedFriend(String, int, boolean, boolean)
     */
    public HighlightedFriend()
    {
        name = "";
        color = 0xFFFFFF;
        onlyPlayers = true;
        outlineFriend = true;
    }

    /**
     * Works like {@link #HighlightedFriend(String, int, boolean, boolean)}, but the booleans default to true.
     * @see #HighlightedFriend()
     * @see #HighlightedFriend(String, int, boolean, boolean)
     */
    public HighlightedFriend(String name, int color)
    {
        this.name = name;
        this.color = color;
        this.onlyPlayers = true;
        this.outlineFriend = true;
    }

    /**
     * Creates a friend with a specific name, color, and booleans.
     * @param name the name of the friend.
     * @param color the highlight/name color for the friend.
     * @param onlyPlayers whether this friend only includes players, as opposed to all entities.
     * @param outlineFriend whether this friend should get highlighted, or just have its name show and be colored.
     * @see #HighlightedFriend()
     * @see #HighlightedFriend(String, int)
     */
    public HighlightedFriend(String name, int color, boolean onlyPlayers, boolean outlineFriend)
    {
        this.name = name;
        this.color = color;
        this.onlyPlayers = onlyPlayers;
        this.outlineFriend = outlineFriend;
    }
}