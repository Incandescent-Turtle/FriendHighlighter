package mod.icy_turtle.friendhighlighter.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CommandUtils
{
	private CommandUtils(){}

	public static String readSpacelessArgumentFrom(StringReader reader, int argBeginning)
	{
		if (!reader.canRead())
			reader.skip();

		while(reader.canRead() && reader.peek() != ' ')
			reader.skip();

		return reader.getString().substring(argBeginning, reader.getCursor());
	}

	public static String readSpacelessArgument(StringReader reader)
	{
		return readSpacelessArgumentFrom(reader, reader.getCursor());
	}

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

	//	eclusive
	public static String readUntil(StringReader reader, int cursorPos)
	{
		final int start = reader.getCursor();
		while(reader.canRead() && reader.getCursor() <= cursorPos)
		{
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor()-1);
	}

	public static String readQuotedString(StringReader reader) throws CommandSyntaxException
	{
		final int argBeginningIndex = reader.getCursor() - 1;
		var remainingCmd = reader.getRemaining();

		//  starts with quote
		if(StringReader.isQuotedStringStart(remainingCmd.charAt(0)))
		{
			final char quotationMark = remainingCmd.charAt(0);
			final int lastIndexOfQuotation = remainingCmd.lastIndexOf(quotationMark);
			//  no other quote marks (just a string starting with a quote)
			if(lastIndexOfQuotation == 0)
			{
				return CommandUtils.readSpacelessArgumentFrom(reader, argBeginningIndex + 1);
			} else
			{
				//  if the ending quote is the last character
				//  or the char after is a space
				if(remainingCmd.length() == lastIndexOfQuotation + 1 || remainingCmd.charAt(lastIndexOfQuotation + 1) == ' ')
				{
					reader.skip();
					var s = CommandUtils.readUntil(reader, argBeginningIndex + lastIndexOfQuotation + 1);
					System.out.println(s);
					return s;
					//  if it isnt the last
				} else {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape()
							.createWithContext(reader, "what");
				}
			}
		} else {
			return CommandUtils.readSpacelessArgument(reader);
		}
	}
}