package mod.icy_turtle.friendhighlighter.util;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

public class ChatUtils
{
	private ChatUtils(){}

	/**
	 * Reloads the chat via {@link ChatHud#reset()} while maintaining scroll position if applicable.
	 */
	public static void refreshChat()
	{
		var chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		var lines = chatHud.scrolledLines;
		FriendHighlighter.logChatMessages = false;
		MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		FriendHighlighter.logChatMessages = true;
		chatHud.scroll(lines);
	}

	/**
	 * Removes all instances of this message from the in-game chat.
	 * @param txt the message to delete
	 * @return whether or not 1 or more items were removed.
	 */
	public static boolean removeAllFromChat(Text txt)
	{
		boolean removed = false;
		var messages = MinecraftClient.getInstance().inGameHud.getChatHud().messages;
		for(var msg : messages.stream().map(line -> line.content()).toList())
		{
			if(msg.equals(txt))
			{
				removed = true;
				messages.remove(msg);
			}
		}
		return removed;
	}
}