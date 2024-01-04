package mod.icy_turtle.friendhighlighter.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.command.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.command.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.command.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AddFriendCommand extends Command
{
	private static final String FRIEND_NAME = "friendName", COLOR = "color", ONLY_PLAYERS = "onlyPlayers", JUST_NAME_TAG = "justNameTag";

	public AddFriendCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		// boolean arguments are optional and default to false
		return literal("add")
				.then(argument(FRIEND_NAME, new PossibleFriendNameArgumentType())
						.executes(this::addFriend)
						.then(argument(COLOR, new ColorArgumentType())
								.executes(this::addFriend)
								.then(argument(ONLY_PLAYERS, new BooleanWithWords("onlyPlayers", "allEntities"))
										.executes(this::addFriend)
										.then(argument(JUST_NAME_TAG, new BooleanWithWords("justNameTag", "nameTagAndOutline"))
												.executes(this::addFriend))))
				.executes(this::addFriend));
	}

	private int addFriend(CommandContext<FabricClientCommandSource> context)
	{
		var friendsMap = FriendsListHandler.getFriendsMap();

		String friendName = context.getArgument(FRIEND_NAME, String.class);
		String color = CommandUtils.getArgumentFromContext(context, COLOR, "#" + Integer.toHexString(FHSettings.getSettings().defaultColor));
		boolean onlyPlayer = CommandUtils.getArgumentFromContext(context, ONLY_PLAYERS, FHSettings.getSettings().defaultPlayersOnly);
		boolean justNameTag = CommandUtils.getArgumentFromContext(context, JUST_NAME_TAG, false);

		MutableText txt = Text.literal("");
		if(!friendsMap.containsKey(friendName))
		{
			txt.append(friendName).append(" ").append(FHUtils.getPositiveMessage("ADDED")).append(" ").append("with color of").append(" ").append(FHUtils.colorText(color, FHUtils.hexToRGB(color)));
		} else
		{
			txt = FHUtils.getPositiveMessage(friendName + " Updated");
		}
		friendsMap.put(friendName, new HighlightedFriend(friendName, FHUtils.hexToRGB(color), onlyPlayer, justNameTag).setEnabled(friendsMap.getOrDefault(friendName, new HighlightedFriend()).isEnabled()));
		cmdHandler.updateLists();
		FriendHighlighter.sendMessage(txt);
		FHConfig.saveConfig();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}
