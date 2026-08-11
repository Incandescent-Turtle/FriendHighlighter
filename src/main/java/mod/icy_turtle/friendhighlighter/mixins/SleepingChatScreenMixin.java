package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin
{
	// used to determine whether a command was sent from chat or from list.
	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void markChatSentTime(KeyInput input, CallbackInfoReturnable<Boolean> cir)
	{
		FriendHighlighter.enterHitAt = System.currentTimeMillis();
	}
}