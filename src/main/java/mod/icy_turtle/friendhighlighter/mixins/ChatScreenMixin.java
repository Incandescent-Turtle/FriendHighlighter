package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ChatScreenMixin
{
	@Inject(method = "keyPressed", at = @At("HEAD"))
	private void markChatSentTime(KeyInput input, CallbackInfoReturnable<Boolean> cir)
	{
		if ((Object) this instanceof ChatScreen)
		{
			FriendHighlighter.enterHitAt = System.currentTimeMillis();
		}
	}
}