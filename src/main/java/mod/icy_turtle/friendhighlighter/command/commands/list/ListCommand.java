package mod.icy_turtle.friendhighlighter.command.commands.list;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ListCommand extends Command
{
	public ListCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("list")
				.then(new AdvancedListCommand(cmdHandler).createCommand())
				.then(new SimpleListCommand(cmdHandler).createCommand())
				.executes(context -> cmdHandler.simpleListChatMsg.sendInChat());
	}

	protected static Text createToggleableName(HighlightedFriend friend)
	{
		return FHUtils.colorText(friend.name, friend.color)
				.styled(style -> style
						.withStrikethrough(!friend.isEnabled())
						.withHoverEvent(CommandUtils.createToolTip(Text.literal(friend.name + " is ").append(FHUtils.getMessageWithConnotation("ENABLED", "DISABLED", friend.isEnabled()).append(" | Click to " + (friend.isEnabled() ? "disable" : "enable")))))
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh toggle \""+friend.name+"\"")));
	}
}
