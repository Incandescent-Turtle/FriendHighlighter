package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedBase;
import mod.icy_turtle.friendhighlighter.config.HighlightedEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	//	changes which color the entitiy should be highlighted in.
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
	private int forceHighlightColor(Entity entity)
	{
		// if it is a projectile that should be highlighted, use owners colour
		if(FriendsListHandler.shouldOutlineProjectile(entity))
		{
			var owner = ((ProjectileEntity)entity).getOwner();
			return getColorOf(owner);
		}

		// use the friend/entity assigned color, or team color if applicable
		if(FriendsListHandler.shouldHighlightEntity(entity))
		{
			return getColorOf(entity);
		}
		return entity.getTeamColorValue();
	}

	//	only do if entity should be highlighted
	// gets the color of an entity from its friend entry, or uses the team color if applicable
	private int getColorOf(Entity entity)
	{
		var friend = FriendsListHandler.getFriendFromEntity(entity);
		// If respecting team colours, and not on a team, the team colour will be used
		if(!FHSettings.getSettings().ignoreTeamColor && entity.getScoreboardTeam() != null)
		{
			return entity.getTeamColorValue();
		}
		return friend.getColor();
	}
}