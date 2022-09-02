package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin
{
	//	stops logging chat messages when updated the list (or else sends whole chat history).
	@Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;logChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
	private void dontLogChatOnRefresh(ChatHud instance, Text message, MessageIndicator indicator)
	{
		if(FriendHighlighter.logChatMessages)
		{
			instance.logChatMessage(message, indicator);
		}
	}
}