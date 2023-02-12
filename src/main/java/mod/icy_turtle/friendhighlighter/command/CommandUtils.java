package mod.icy_turtle.friendhighlighter.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.arguments.StringListArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

/**
 * 	A utility class for this mod providing helpful methods for commands.
 */
public class CommandUtils
{
	private CommandUtils(){}

	/**
	 * Reads all characters until a space is hit, or the end of a command, starting from current cursor pos.
	 * @param reader the string reader for the command.
	 * @return all characters until a space or the end of the command.
	 */
	public static String readSpacelessArgument(StringReader reader)
	{
		int argBeginning = reader.getCursor();

		if (!reader.canRead())
			reader.skip();

		while(reader.canRead() && reader.peek() != ' ')
			reader.skip();

		return reader.getString().substring(argBeginning, reader.getCursor());
	}

	/**
	 * Attempts to read argument from a {@link CommandContext}, if it doesn't exist it uses the default value.
	 * @param context the command context.
	 * @param name the name of the argument, case-sensitive.
	 * @param defaultValue if the argument doesn't exist, this value will be returned.
	 * @return returns either the value of the argument if it exists, or the default value otherwise.
	 * @param <T> the type of the parameter (based off defaultValue).
	 * @see CommandContext#getArgument(String, Class)
	 */
	public static <T> T getArgumentFromContext(CommandContext<FabricClientCommandSource> context, String name,
			T defaultValue)
	{
		T val;
		try {
			val = (T) context.getArgument(name, defaultValue.getClass());
		} catch(Exception e) {
			val = defaultValue;
		}
		return val;
	}

	/**
	 * Reads an argument until the specified cursorPos (exclusive).
	 * @param reader the reader for the command.
	 * @param cursorPos the cursor position to read until, exclusive, cursor ends on this position.
	 * @return the string starting at the current cursor pos (inclusive) and ending at the last cursor pos (exclusive).
	 */
	public static String readUntil(StringReader reader, int cursorPos)
	{
		final int start = reader.getCursor();
		while(reader.canRead() && reader.getCursor() <= cursorPos)
		{
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor()-1);
	}

	/**
	 * Reads a string that can be quoted, or unquoted if it contains no spaces. It can be enclosed in either " or ', and can contain other quotation marks. <br>
	 * <p style="color:red;">Do not use if following arguments may contain quotes.</p>
	 * @param reader the reader.
	 * @return the string, not including the closing quotes if applicable.
	 * @throws CommandSyntaxException throws if it is not a proper quoted string enclosed by " " or ' '.
	 */
	public static String readQuotedOrUnquotedString(StringReader reader) throws CommandSyntaxException
	{
		final int argBeginning = reader.getCursor();
		//	starting at the cursor, the remaining part of the command
		var remainingCmd = reader.getRemaining();
		//  if starts with quote
		if(StringReader.isQuotedStringStart(remainingCmd.charAt(0)))
		{
			// whether " or '
			final char quotationMark = remainingCmd.charAt(0);
			//	the index of the last matching quotation in the remaining command, 0 if there is only the starting one
			final int lastRelativeIndexOfQuotation = remainingCmd.lastIndexOf(quotationMark);

			// if no other quote marks (just a normal string starting with a quotation mark)
			if(lastRelativeIndexOfQuotation == 0)
			{
				return CommandUtils.readSpacelessArgument(reader);
			} else {
				//  if the ending quote is the last character in the command or the char after is a space
				if(remainingCmd.length() == lastRelativeIndexOfQuotation + 1 || remainingCmd.charAt(lastRelativeIndexOfQuotation + 1) == ' ')
				{
					//	skips first quotation
					reader.skip();
					//	reads until the last quote
					return CommandUtils.readUntil(reader, argBeginning + lastRelativeIndexOfQuotation);
				} else {
					//  throws if the closing quotation mark is followed by non-space characters
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader);
				}
			}
		} else {
			return CommandUtils.readSpacelessArgument(reader);
		}
	}

	/**
	 * Creates a @{link StringListArgumentType} using the friends list to populate the list.
	 * @return an argument that will supply and restrict input to the friends list at time of usage.
	 */
	public static RequiredArgumentBuilder<FabricClientCommandSource, String> createExistingFriendArgument(String argName)
	{
		return argument(argName,
				new StringListArgumentType(
						() -> FriendsListHandler.getFriendsMap().keySet().stream().toList(),
						name -> Text.literal(name + " cannot be removed as they are not on your friends list."),
						List.of("icy_turtle", "Player123", "Xx_Notch_xX")
				).setSuggestWithQuotes(true)
		);
	}

	/**
	 * Creates a {@link HoverEvent} tooltip for chat messages.
	 * @param txt the tooltip to be displayed.
	 * @return the event to be used to create the desired tooltip.
	 * @see #createToolTip(String)
	 */
	public static HoverEvent createToolTip(MutableText txt)
	{
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, FHSettings.getSettings().tooltipsEnabled ? txt : null);
	}

	/**
	 * Creates a {@link HoverEvent} tooltip for chat messages.
	 * @param str the tooltip to be displayed.
	 * @return the event to be used to create the desired tooltip.
	 * @see #createToolTip(MutableText)
	 */
	public static HoverEvent createToolTip(String str)
	{
		return createToolTip(Text.literal(str));
	}

	/**
	 * Adds a tool tip to the parent
	 * @param parent the parent to add the tooltip to
	 * @param tooltip the tooltip to show upon hover
	 * @return the mutated parent
	 * @see #addToolTip(MutableText, String)
	 * @see #addHoverAndClickEvent(MutableText, MutableText, String)
	 * @see #addHoverAndClickEvent(MutableText, String, String)
	 */
	public static MutableText addToolTip(MutableText parent, MutableText tooltip)
	{
		return parent.styled(style -> style.withHoverEvent(createToolTip(tooltip)));
	}

	/**
	 * Adds a tool tip to the parent
	 * @param parent the parent to add the tooltip to
	 * @param tooltip the tooltip to show upon hover
	 * @return the mutated parent
	 * @see #addToolTip(MutableText, MutableText)
	 * @see #addHoverAndClickEvent(MutableText, MutableText, String)
	 * @see #addHoverAndClickEvent(MutableText, String, String)
	 */
	public static MutableText addToolTip(MutableText parent, String tooltip)
	{
		return addToolTip(parent, Text.literal(tooltip));
	}

	/**
	 * Refreshes in-game chat, and if open will not affect the position in chat.
	 */
	public static void refreshChatWithPreservingScroll()
	{
		var chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		var lines = chatHud.scrolledLines;
		// TODO dont think that this is needed anymore, but maybe keep for porting? - the logging that is
		FriendHighlighter.logChatMessages = false;
		MinecraftClient.getInstance().inGameHud.getChatHud().reset();
		FriendHighlighter.logChatMessages = true;
		chatHud.scroll(lines);
	}

	/**
	 * Removes the first instance of this text object in chat.
	 * @param content the text content to search for
	 * @return whether a message was removed from chat
	 */
	public static boolean removeMessageWithContent(Text content)
	{
		var chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		boolean deleted = false;
		for(var message : chatHud.messages)
		{
			if(message.content() == content)
			{
				chatHud.messages.remove(message);
				deleted = true;
				break;
			}
		}
		refreshChatWithPreservingScroll();
		return deleted;
	}

	/**
	 * Adds a text hover event and sends a command on click of the parent
	 * @param parent the text
	 * @param hoverText the text to be displayed on hover
	 * @param command the command that is sent in chat on click
	 * @return the mutated parent
	 * @see #addHoverAndClickEvent(MutableText, MutableText, String)
	 * @see #addToolTip(MutableText, String)
	 * @see #addToolTip(MutableText, MutableText)
	 */
	public static MutableText addHoverAndClickEvent(MutableText parent, MutableText hoverText, String command)
	{
		return parent.styled(style ->
				style.withHoverEvent(CommandUtils.createToolTip(hoverText))
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
	}

	/**
	 * Adds a text hover event and sends a command on click of the parent
	 * @param parent the text
	 * @param hoverText the text to be displayed on hover
	 * @param command the command that is sent in chat on click
	 * @return the mutated parent
	 * @see #addHoverAndClickEvent(MutableText, MutableText, String)
	 * @see #addToolTip(MutableText, String)
	 * @see #addToolTip(MutableText, MutableText)
	 */
	public static MutableText addHoverAndClickEvent(MutableText parent, String hoverText, String command)
	{
		return addHoverAndClickEvent(parent, Text.literal(hoverText), command);
	}
}