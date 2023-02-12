package mod.icy_turtle.friendhighlighter.command;

import com.mojang.brigadier.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class ChatMessage
{
	private final MutableText content;
	private Supplier<MutableText> contentCreator;

	public ChatMessage(Supplier<MutableText> contentCreator)
	{
		this.contentCreator = contentCreator;
		content = Text.literal("");
	}

	/**
	 * 
	 * Uses this messages supplier to re-generate the text and refresh chat via {@link CommandUtils#refreshChatWithPreservingScroll()}
	 * @return this message
	 */
	public ChatMessage updateContent()
	{
		content.getSiblings().clear();
		content.getSiblings().addAll(contentCreator.get().getSiblings());
		CommandUtils.refreshChatWithPreservingScroll();
		return this;

	}

	/**
	 * Removes this message from chat if it exists via {@link CommandUtils#removeMessageWithContent(Text)}
	 * @see CommandUtils#removeMessageWithContent(Text)
	 * @return
	 */
	public boolean removeFromChat()
	{
		return CommandUtils.removeMessageWithContent(content);
	}

	/**
	 * Removes the message from chat, regenerates message content, then sends the message to the player
	 * @return this message
	 * @see #removeFromChat() 
	 * @see #updateContent() 
	 * @see net.minecraft.entity.player.PlayerEntity#sendMessage(Text) 
	 */
	public int sendInChat()
	{
		removeFromChat();
		updateContent();
		MinecraftClient.getInstance().player.sendMessage(content);
		return Command.SINGLE_SUCCESS;
	}
}