package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
	@Inject(method = "isGlowing", at = @At(value = "HEAD"), cancellable = true)
	public void overrideIsGlowing(CallbackInfoReturnable<Boolean> cir)
	{
		var friend = FriendsListHandler.getFriendFromEntity((Entity) (Object) this);
		if(FriendsListHandler.shouldHighlightEntity((Entity) (Object) this) && !friend.isJustNameTag())
		{
			cir.setReturnValue(true);
		}
	}
}
