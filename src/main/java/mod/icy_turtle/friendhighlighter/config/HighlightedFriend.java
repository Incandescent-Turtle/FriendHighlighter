package mod.icy_turtle.friendhighlighter.config;

public class HighlightedFriend
{
    public String name;
    public int color;
    public boolean onlyPlayers;
    public boolean outlineFriend;

    public HighlightedFriend()
    {
        name = "";
        color = 0xFFFFFF;
        onlyPlayers = true;
        outlineFriend = true;
    }

    public HighlightedFriend(String name, int color)
    {
        this.name = name;
        this.color = color;
        this.onlyPlayers = true;
        this.outlineFriend = true;
    }

    public HighlightedFriend(String name, int color, boolean onlyPlayers, boolean outlineFriend)
    {
        this.name = name;
        this.color = color;
        this.onlyPlayers = onlyPlayers;
        this.outlineFriend = outlineFriend;
    }
}