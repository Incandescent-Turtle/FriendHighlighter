package mod.icy_turtle.friendhighlighter.config;

/**
 * Represents an entity that can be highlighted through this mod.
 */
public class HighlightedFriend extends HighlightedBase
{
    /**
     * Whether this only represents players, instead of all entities.
     */
    private boolean onlyPlayers;

    /**
     * Whether this friend should be highlighted, or just have their name tag show and be colored.
     */
    private boolean justNameTag;

    /**
     * Creates a default friend with initialized values.
     * @see #HighlightedFriend(String, int, boolean, boolean)
     */
    public HighlightedFriend()
    {
        super(0xFFFFFF, "", true);
        onlyPlayers = true;
        justNameTag = true;
    }

    /**
     * Creates a friend with a specific name, color, and booleans.
     * @param name the name of the friend.
     * @param color the highlight/name color for the friend.
     * @param onlyPlayers whether this friend only includes players, as opposed to all entities.
     * @param justNameTag whether this friend should get highlighted, or just have its name show and be colored.
     * @see #HighlightedFriend()
     */
    public HighlightedFriend(String name, int color, boolean onlyPlayers, boolean justNameTag)
    {
        super(color, name, true);
        this.onlyPlayers = onlyPlayers;
        this.justNameTag = justNameTag;
    }

    public boolean isOnlyPlayers()
    {
        return onlyPlayers;
    }

    public void setOnlyPlayers(boolean onlyPlayers)
    {
        this.onlyPlayers = onlyPlayers;
    }

    @Override
    public boolean isJustNameTag()
    {
        return justNameTag;
    }

    public void setJustNameTag(boolean justNameTag)
    {
        this.justNameTag = justNameTag;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}