package mod.icy_turtle.friendhighlighter.config;

public abstract class HighlightedBase
{
	private int color;
	protected String name;

	public HighlightedBase(int color, String name)
	{
		this.color = color;
		this.name = name;
	}
	final public int getColor()
	{
		return color;
	}

	final public void setColor(int color)
	{
		this.color = color;
	}

	final public String getName()
	{
		return name;
	}
	public abstract boolean isEnabled();
	public abstract boolean isJustNameTag();
	public abstract boolean isOnlyPlayers();
}
