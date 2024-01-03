package mod.icy_turtle.friendhighlighter.command.commands.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.command.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.command.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.command.arguments.StringListArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SettingsSetCommand extends Command
{
	private static final String DISPLAY_METHOD = "displayMethod", VALUE = "value";
	public SettingsSetCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("set")
				.then(literal("messageDisplayMethod")
						.then(argument(DISPLAY_METHOD, new StringListArgumentType(() -> Arrays.stream(FHSettings.MessageDisplayMethod.values()).map(dm -> dm.name()).collect(Collectors.toList())))
								.executes(this::setDisplayMethod)))
				.then(literal("tooltipVisibility")
						.then(argument(VALUE, new BooleanWithWords("visible", "hidden"))
								.executes(this::setTooltipVisibility)))
				.then(literal("highlightInvisibleFriends")
						.then(argument(VALUE, new BooleanWithWords("enabled", "disabled"))
								.executes(this::setHighlightInvisibleFriends)))
				.then(literal("defaultColor")
						.then(argument(VALUE, new ColorArgumentType())
								.executes(this::setDefaultColor)))
				.then(literal("defaultPlayersOnly")
						.then(argument(VALUE, new BooleanWithWords("onlyPlayers", "allEntities"))
								.executes(this::setDefaultPlayersOnly)));
	}

	private int setDisplayMethod(CommandContext<FabricClientCommandSource> context)
	{
		var arg = context.getArgument(DISPLAY_METHOD, String.class);
		var settings = FHSettings.getSettings();
		settings.messageDisplayMethod = FHSettings.MessageDisplayMethod.valueOf(arg);
		FriendHighlighter.sendMessage(Text.literal("Display method set to ").append(Text.literal(arg).styled(style -> style.withBold(true).withColor(Formatting.GREEN))));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setTooltipVisibility(CommandContext<FabricClientCommandSource> context)
	{
		FHSettings.getSettings().tooltipsEnabled = context.getArgument(VALUE, Boolean.class);
		FriendHighlighter.sendMessage(Text.literal("Tooltips are ").append(FHUtils.getMessageWithConnotation("visible", "hidden", FHSettings.getSettings().tooltipsEnabled)));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightInvisibleFriends(CommandContext<FabricClientCommandSource> context)
	{
		FHSettings.getSettings().highlightInvisibleFriends = context.getArgument(VALUE, Boolean.class);
		FriendHighlighter.sendMessage(Text.literal("Highlighting invisible friends is ").append(FHUtils.getMessageWithConnotation("enabled", "disabled", FHSettings.getSettings().highlightInvisibleFriends)));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setDefaultColor(CommandContext<FabricClientCommandSource> context)
	{
		String color = CommandUtils.getArgumentFromContext(context, VALUE, "#FFFFFF");
		FHSettings.getSettings().defaultColor = FHUtils.hexToRGB(color);
		FriendHighlighter.sendMessage(Text.literal("Default highlight color set to ").append(FHUtils.colorText(color, FHUtils.hexToRGB(color))));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setDefaultPlayersOnly(CommandContext<FabricClientCommandSource> context)
	{
		boolean playersOnly = CommandUtils.getArgumentFromContext(context, VALUE, true);
		FHSettings.getSettings().defaultPlayersOnly = playersOnly;
		FriendHighlighter.sendMessage(Text.literal("When not specified, a friend added by commands will now " + (playersOnly ? "only highlight players." : "highlight any entity with that name.")));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}
