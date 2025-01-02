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
	public boolean tooltipsEnabled = true;
	public boolean highlightInvisibleFriends = true;
	public Integer defaultColor = 0xFFFFFF;
	public boolean defaultPlayersOnly = false;
	public boolean enhancedNametags = true;
	public boolean highlightThroughWalls = true;
	public boolean highlightWhileSneaking = true;
	// When friend is on a team, respect the team colour.
	public boolean ignoreTeamColor = true;
	// Whether projectiles shot by friends/entities will be highlighted in their highlight colour.
	public boolean highlightProjectiles = false;
	// whether a mob will get highlighted temporarily after getting hit by the player
	public boolean highlightMobsYouHit = false;
	// default color mobs highlight when hit by player
	public Integer hitHighlightColor = 0xFFFF00;
	// amount of seconds it will stay up
	public Integer hitHighlightSeconds = 3;
	// highlights every entity (if enabled, skips every other check for highlighting)
	public boolean highlightEverything = false;

	public enum MessageDisplayMethod
	{
		ACTION_BAR, CHAT, BOTH
	}

	public MessageDisplayMethod getNextDisplayMethod()
	{
		var vals = List.of(MessageDisplayMethod.values());
		var pos = vals.indexOf(messageDisplayMethod);
		pos += 1;
		pos %= vals.size();
		return vals.get(pos);
	}
}
