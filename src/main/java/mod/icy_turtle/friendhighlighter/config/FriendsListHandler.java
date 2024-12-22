package mod.icy_turtle.friendhighlighter.config;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;


public class FriendsListHandler
{
	/**
	 * The map to be used throughout the mod to check which names are on the friends list.
	 */
	public LinkedHashMap<String, HighlightedFriend> friendsMap = new LinkedHashMap<>();
	public LinkedHashMap<String, HighlightedEntity> entityMap = new LinkedHashMap<>();

	public static LinkedHashMap<String, HighlightedFriend> getFriendsMap()
	{
		return FHConfig.getFriendsListHandler().friendsMap;
	}

	public static LinkedHashMap<String, HighlightedEntity> getEntityMap()
	{
		return FHConfig.getFriendsListHandler().entityMap;
	}

	public static void setFriendsMap(LinkedHashMap<String, HighlightedFriend> map)
	{
		FHConfig.getFriendsListHandler().friendsMap = map;
	}

	/**
	 * Returns the instance of {@link HighlightedBase} associated to this entity. If it exists in the friends list, that entry is returned.
	 * @param entity the entity to use to get the friend.
	 * @return the associated {@link HighlightedBase} instance, or null if there isn't one.
	 */
	public static @Nullable HighlightedBase getFriendFromEntity(Entity entity)
	{
		// Checks friends list for the entity's name
		var friend = getFriendsMap().get(entity.getName().getString());
		if(friend != null)
		{
			return friend;
		}

		// Checks to see if this entity is tagged
		return getEntityMap().get(FHUtils.getNameFromEntityType(entity.getType()).getString());
	}

	/**
	 * Whether this entity should be highlighted currently
	 * @param entity the entity to test.
	 * @return whether this entity should be highlighted. Returns false if entity is null.
	 */
	public static boolean shouldHighlightEntity(@Nullable Entity entity)
	{
		if(entity == null)
		{
			return false;
		}
		if(!FriendHighlighter.isHighlighterEnabled)
		{
			return false;
		}

		var friend = getFriendFromEntity(entity);

		if((friend == null || !friend.isEnabled()))
		{
			return false;
		}

		var settings = FHSettings.getSettings();

		if(entity instanceof PlayerEntity || !friend.isOnlyPlayers())
		{
			if(settings.highlightWhileSneaking || !entity.isSneaky())
			{
				if(settings.highlightInvisibleFriends || !entity.isInvisible())
				{
					if(settings.highlightThroughWalls || FHUtils.canSeeEntity(MinecraftClient.getInstance().player, entity))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean shouldRenderNametag(Entity entity)
	{
		if(!shouldHighlightEntity(entity))
		{
			return false;
		}

		if(entity.hasCustomName())
		{
			return true;
		}

		return false;
	}

	// Returns whether this 1. is a projectile 2. projectile highlighting is on and 3. projectile was shot/thrown by a friend/entity that is highlighted currently
	public static boolean shouldOutlineProjectile(@Nullable Entity entity)
	{
		if(!FHSettings.getSettings().highlightProjectiles)
		{
			return false;
		}
		if(entity instanceof ProjectileEntity projectile)
		{
			var owner = projectile.getOwner();
			return shouldHighlightEntity(owner) && !FriendsListHandler.getFriendFromEntity(owner).isJustNameTag();
		}
		return false;
	}
}