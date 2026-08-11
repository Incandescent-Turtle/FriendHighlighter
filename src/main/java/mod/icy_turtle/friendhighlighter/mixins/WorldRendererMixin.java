package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class WorldRendererMixin
{
	// changes which color the entity should be highlighted in — moved here from WorldRenderer
	// since getTeamColorValue() is now read inside EntityRenderer#updateRenderState, not WorldRenderer#render
	@Redirect(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
	private int forceHighlightColor(Entity entity)
	{
		if (FriendHighlighter.isHighlighterEnabled && FHSettings.getSettings().highlightMobsYouHit && entity instanceof LivingEntity le)
		{
			var hitMap = FriendsListHandler.getRecentHitMap();
			if (hitMap.containsKey(entity))
			{
				return FHSettings.getSettings().hitHighlightColor;
			}
		}
		if (FriendsListHandler.shouldOutlineProjectile(entity))
		{
			var owner = ((ProjectileEntity) entity).getOwner();
			return getColorOf(owner);
		}
		if (FriendsListHandler.shouldHighlightEntity(entity))
		{
			return getColorOf(entity);
		}
		return entity.getTeamColorValue();
	}

	private int getColorOf(Entity entity)
	{
		var friend = FriendsListHandler.getFriendFromEntity(entity);
		if (!FHSettings.getSettings().ignoreTeamColor && entity.getScoreboardTeam() != null)
		{
			return entity.getTeamColorValue();
		}
		return friend.getColor();
	}
}