package mod.icy_turtle.friendhighlighter.command.commands.list;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SimpleListCommand extends Command
{
	public SimpleListCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("simple")
				.executes(context -> cmdHandler.simpleListChatMsg.sendInChat());
	}

	public static MutableText createSimpleList()
	{
		MutableText txt = Text.literal("").append(CommandUtils.addHoverAndClickEvent(
				FHUtils.getMessageWithConnotation("Friends List: ", FriendHighlighter.isHighlighterEnabled),
				"Click to toggle Friend Highlighter",
				"/fh toggle"));
		var map = FriendsListHandler.getFriendsMap();
		if(map.size() == 0)
			return txt.append(" Empty.");
		var itr = map.entrySet().iterator();
		while(itr.hasNext())
		{
			var friend = itr.next().getValue();
			txt.append(ListCommand.createToggleableName(friend))
					.append(itr.hasNext() ? ", " : ".");
		}
		return txt;
	}
}
