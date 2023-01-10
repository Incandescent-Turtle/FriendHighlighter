package mod.icy_turtle.friendhighlighter.config;

import java.util.List;

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
	public MessageDisplayMethod getNextDisplayMethod()
	{
		var vals = List.of(FHSettings.MessageDisplayMethod.values());
		var pos = vals.indexOf(messageDisplayMethod);
		pos += 1;
		pos %= vals.size();
		return vals.get(pos);
	}

	public boolean tooltipsEnabled = true;
	public boolean highlightInvisibleFriends = true;
	public enum MessageDisplayMethod
	{
		ACTION_BAR, CHAT, BOTH
	}
}