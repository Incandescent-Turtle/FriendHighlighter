package mod.icy_turtle.friendhighlighter.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClearCommand extends Command
{
	public ClearCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("clear")
				.executes(this::clearFriendsList);
	}

	private int clearFriendsList(CommandContext<FabricClientCommandSource> context)
	{
		var friendsMap = FriendsListHandler.getFriendsMap();
		friendsMap.clear();
		FriendHighlighter.sendMessage(FHUtils.getNegativeMessage("Cleared Friends List"));
		cmdHandler.updateLists();
		FHConfig.saveConfig();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}
