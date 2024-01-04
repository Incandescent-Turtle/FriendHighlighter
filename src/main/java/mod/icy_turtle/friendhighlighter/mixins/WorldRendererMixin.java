package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
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
		if(FriendHighlighter.isHighlighterEnabled)
		{
			var friend = FriendsListHandler.getFriendFromEntity(entity);
			if(FriendsListHandler.shouldHighlightEntity(entity))
			{
				return friend.color;
			}
		}
		return entity.getTeamColorValue();
	}

	// forces the outline on entities that should be highlighted
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
	public boolean forceHighlight(MinecraftClient client, Entity entity)
	{
		if(FriendHighlighter.isHighlighterEnabled)
		{
			var friend = FriendsListHandler.getFriendFromEntity(entity);
			if(FriendsListHandler.shouldHighlightEntity(entity) && !friend.justNameTag)
			{
				return true;
			}
		}
		return client.hasOutline(entity);
	}
}