package mod.icy_turtle.friendhighlighter.config;

public abstract class HighlightedBase
{
	private int color;
	protected String name;
	private boolean enabled;

	public HighlightedBase(int color, String name, boolean enabled)
	{
		this.color = color;
		this.name = name;
		this.enabled = enabled;
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
	final public boolean isEnabled()
	{
		return enabled;
	}

	final public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public abstract boolean isJustNameTag();
	public abstract boolean isOnlyPlayers();
}
