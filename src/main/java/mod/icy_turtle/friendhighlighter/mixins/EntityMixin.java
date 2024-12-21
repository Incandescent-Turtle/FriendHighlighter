package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;


@Mixin(Entity.class)
public class EntityMixin
{
	// TODO this seems to be firing before load and sticking these things permenantly
	// this actually makes the thing glow i think
	@Inject(method = "isGlowing", at = @At(value = "HEAD"), cancellable = true)
	public void overrideIsGlowing(CallbackInfoReturnable<Boolean> cir)
	{
		var friend = FriendsListHandler.getFriendFromEntity((Entity) (Object) this);
		if(FriendsListHandler.shouldHighlightEntity((Entity) (Object) this) && !friend.isJustNameTag())
		{
			System.out.println("ret true");
			System.out.println(MinecraftClient.getInstance().player);
			System.out.println(FriendHighlighter.isHighlighterEnabled);
			cir.setReturnValue(true);
		}
	}
}
