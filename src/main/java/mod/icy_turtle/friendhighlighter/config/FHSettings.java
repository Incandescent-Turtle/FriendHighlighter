package mod.icy_turtle.friendhighlighter.config;

import mod.icy_turtle.friendhighlighter.util.FHUtils;

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