package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	// This tells the game that this is glowing, which is needed for entity culling compat
	@Inject(method = "hasOutline", at = @At(value = "HEAD"), cancellable = true)
	public void overrideHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir)
	{
		if(FriendHighlighter.isHighlighterEnabled && FHSettings.getSettings().highlightEverything)
		{
			cir.setReturnValue(true);
		} else {
			if(FriendHighlighter.isHighlighterEnabled && FHSettings.getSettings().highlightMobsYouHit && entity instanceof LivingEntity le)
			{
				if(FriendsListHandler.getRecentHitMap().containsKey(le))
				{
					cir.setReturnValue(true);
				}
			}

			if(FriendsListHandler.shouldOutlineProjectile(entity))
			{
				cir.setReturnValue(true);
			}

			var friend = FriendsListHandler.getFriendFromEntity(entity);
			if(FriendsListHandler.shouldHighlightEntity(entity) && !friend.isJustNameTag())
			{
				cir.setReturnValue(true);
			}
		}
	}
}
