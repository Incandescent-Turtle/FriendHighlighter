package mod.icy_turtle.friendhighlighter.config;

/**
 * 	Holds all the settings for the mod; a typical config.
 */
public class FHSettings
{
	public static FHSettings getSettings()
	{
		return FHConfig.getSettings();
	}

	public MessageDisplayMethod messageDisplayMethod = MessageDisplayMethod.ACTION_BAR;

	public enum MessageDisplayMethod
	{
		ACTION_BAR, CHAT, BOTH;
	}
}