package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin
{
	//	used to determine whether a command was send from chat or from list.
	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SleepingChatScreen;sendMessage(Ljava/lang/String;Z)Z", shift = At.Shift.BEFORE))
	private void markChatSentTime(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir)
	{
		FriendHighlighter.enterHitAt = System.currentTimeMillis();
	}
}