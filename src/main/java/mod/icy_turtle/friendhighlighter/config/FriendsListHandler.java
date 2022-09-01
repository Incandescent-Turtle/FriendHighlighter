package mod.icy_turtle.friendhighlighter.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class FriendsListHandler
{
	/**
	 * The map to be used throughout the mod to check which names are on the friends list.
	 */
	public LinkedHashMap<String, HighlightedFriend> friendsMap = new LinkedHashMap<>();

	public static LinkedHashMap<String, HighlightedFriend> getFriendsMap()
	{
		return FHConfig.getFriendsListHandler().friendsMap;
	}

	public static void setFriendsMap(LinkedHashMap<String, HighlightedFriend> map)
	{
		FHConfig.getFriendsListHandler().friendsMap = map;
	}

	/**
	 * Returns the instance of {@link HighlightedFriend} associated to this entity via {@link Entity#getName()}.
	 * @param entity the entity to use to get the friend.
	 * @return the associated {@link HighlightedFriend} instance, or null if there isn't one.
	 */
	public static @Nullable HighlightedFriend getFriendFromEntity(Entity entity)
	{
		return getFriendsMap().get(entity.getName().getString());
	}

	/**
	 * Whether this entity should be affected by either outlining or name rendering.
	 * @param entity the entity to test.
	 * @return whether this entity should be highlighted.
	 */
	public static boolean shouldHighlightEntity(Entity entity)
	{
		var friend = getFriendFromEntity(entity);
		return friend != null && friend.isEnabled() && (entity instanceof PlayerEntity || !friend.onlyPlayers);
	}
}