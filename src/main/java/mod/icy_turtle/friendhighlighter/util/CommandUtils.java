package mod.icy_turtle.friendhighlighter.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

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
}