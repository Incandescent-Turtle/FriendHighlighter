package mod.icy_turtle.friendhighlighter.config;

public class HighlightedPlayer
{
    public String name;
    public int color;

    public HighlightedPlayer()
    {
        name = "";
        color = 0xFFFFFF;
    }

    public HighlightedPlayer(String name, int color)
    {
        this.name = name;
        this.color = color;
    }
}