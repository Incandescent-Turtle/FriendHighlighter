package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
	private int forceHighlightColor(Entity entity)
	{
		if(FriendHighlighter.isHighlighterEnabled)
		{
			var friend = FHConfig.getInstance().getFriendFromEntity(entity);
			if(friend != null && (entity instanceof PlayerEntity || !friend.onlyPlayers))
			{
				return FHConfig.getInstance().getColorOrDefault(entity);
			}
		}
		return entity.getTeamColorValue();
	}
}