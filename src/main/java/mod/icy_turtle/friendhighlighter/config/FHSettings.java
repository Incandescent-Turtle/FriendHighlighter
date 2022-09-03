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

	/**
	 * How messages like "player added" or "player removed" are displayed to the user.
	 */
	public NotificationMethod notificationMethod = NotificationMethod.ACTION_BAR;
	public boolean toolTipsEnabled = true;
	public enum NotificationMethod
	{
		ACTION_BAR, CHAT, BOTH;
	}
}