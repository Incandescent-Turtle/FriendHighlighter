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
     * Whether this friend should be affected by the highlighter.
     */
    private boolean enabled;

    /**
     * Creates a default friend with initialized values.
     * @see #HighlightedFriend(String, int)
     * @see #HighlightedFriend(String, int, boolean, boolean)
     */
    public HighlightedFriend()
    {
        super(0xFFFFFF, "");
        onlyPlayers = true;
        justNameTag = true;
        enabled = true;
    }

    /**
     * Works like {@link #HighlightedFriend(String, int, boolean, boolean)}, but the booleans default to true.
     * @see #HighlightedFriend()
     * @see #HighlightedFriend(String, int, boolean, boolean)
     */
    public HighlightedFriend(String name, int color)
    {
        super(color, name);
        this.onlyPlayers = true;
        this.justNameTag = true;
    }

    /**
     * Creates a friend with a specific name, color, and booleans.
     * @param name the name of the friend.
     * @param color the highlight/name color for the friend.
     * @param onlyPlayers whether this friend only includes players, as opposed to all entities.
     * @param justNameTag whether this friend should get highlighted, or just have its name show and be colored.
     * @see #HighlightedFriend()
     * @see #HighlightedFriend(String, int)
     */
    public HighlightedFriend(String name, int color, boolean onlyPlayers, boolean justNameTag)
    {
        super(color, name);
        this.onlyPlayers = onlyPlayers;
        this.justNameTag = justNameTag;
        this.enabled = true;
    }

    public HighlightedFriend setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
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