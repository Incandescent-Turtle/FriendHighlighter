package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	@Inject(method = "hasOutline", at = @At(value = "HEAD"), cancellable = true)
	public void overrideHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir)
	{
		var friend = FriendsListHandler.getFriendFromEntity(entity);
		if(FriendsListHandler.shouldHighlightEntity(entity) && !friend.isJustNameTag())
		{
			cir.setReturnValue(true);
		}
	}
}
