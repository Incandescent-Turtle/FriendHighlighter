package mod.icy_turtle.friendhighlighter.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedBase;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RemoveCommand extends Command
{
	private static final String NAME = "name";
	public RemoveCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("remove")
				.then(literal("friend")
				.then(CommandUtils.createExistingFriendArgument(NAME)
						.executes(c -> remove(c, FriendsListHandler.getFriendsMap()))))

				.then(literal("entity")
						.then(CommandUtils.createExistingEntityArgument(NAME)
						.executes(c -> remove(c, FriendsListHandler.getEntityMap()))));
	}

	private int remove(CommandContext<FabricClientCommandSource> context, Map<String, ? extends HighlightedBase> map)
	{
		var friendsMap = FriendsListHandler.getFriendsMap();
		String friendName = context.getArgument(NAME, String.class);
		friendsMap.remove(friendName);

		MutableText txt = Text.literal("");
		txt.append(friendName)
				.append(" ")
				.append(FHUtils.getNegativeMessage("REMOVED"))
				.append(" ")
				.append(Text.of("from friends list"));
		cmdHandler.updateLists();
		FriendHighlighter.sendMessage(txt);
		FHConfig.saveConfig();
		if(friendsMap.size() != 0)
			MinecraftClient.getInstance().inGameHud.getChatHud().scroll(-2);
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}
