package mod.icy_turtle.friendhighlighter.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ToggleCommand extends Command
{
	private static final String FRIEND_NAME = "friendName";

	public ToggleCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		//  toggles either the highlighter or an individual friend
		//  toggles a friend
		return literal("toggle")
			.then(CommandUtils.createExistingFriendArgument(FRIEND_NAME)
					.executes(this::toggleFriend))
			.executes(ctx -> FriendHighlighter.toggleHighlight());
	}

	private int toggleFriend(CommandContext<FabricClientCommandSource> context)
	{
		String friendName = context.getArgument(FRIEND_NAME, String.class);
		var friend = FriendsListHandler.getFriendsMap().get(friendName);
		if(friend != null)
		{
			friend.setEnabled(!friend.isEnabled());
			cmdHandler.updateLists();
			FriendHighlighter.sendMessage(Text.literal(friendName)
					.append(" ")
					.append(FHUtils.getMessageWithConnotation("ENABLED", "DISABLED", friend.isEnabled()))
			);
		}
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}