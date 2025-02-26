package mod.icy_turtle.friendhighlighter.command.commands.settings;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
								.executes(this::setDefaultPlayersOnly)))

				.then(literal("enhancedNametags")
						.then(argument(VALUE, new BooleanWithWords("enhanced", "normal"))
								.executes(this::setEnhancedNametags)))

				.then(literal("highlightThroughBlocks")
						.then(argument(VALUE, new BooleanWithWords("highlight", "dontHighlight"))
								.executes(this::setHighlightThroughBlocks)))

				.then(literal("highlightWhileSneaking")
						.then(argument(VALUE, new BooleanWithWords("highlight", "dontHighlight"))
								.executes(this::setHighlightWhileSneaking)))

				.then(literal("ignoreTeamColor")
						.then(argument(VALUE, new BooleanWithWords("ignoreTeamColor", "useTeamColor"))
								.executes(this::setRespectTeamColors)))

				.then(literal("highlightThrownProjectiles")
						.then(argument(VALUE, new BooleanWithWords("highlight", "dontHighlight"))
								.executes(this::setHighlightProjectiles)))

				.then(literal("highlightMobsYouHit")
						.then(argument(VALUE, new BooleanWithWords("highlight", "dontHighlight"))
								.executes(this::setHighlightMobsYouHit)))

				.then(literal("hitHighlightColor")
						.then(argument(VALUE, new ColorArgumentType())
								.executes(this::setHitHighlightColor)))

				.then(literal("hitHighlightSeconds")
						.then(argument(VALUE, IntegerArgumentType.integer(1, 100))
								.executes(this::setHitHighlightSeconds)))

				.then(literal("highlightAllEntities")
						.then(argument(VALUE, new BooleanWithWords("highlight", "dontHighlight"))
								.executes(this::setHighlightAllEntities)));
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

	private int setEnhancedNametags(CommandContext<FabricClientCommandSource> context)
	{
		boolean enhancedNametags = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().enhancedNametags = enhancedNametags;
		FriendHighlighter.sendMessage(Text.literal("Enhanced nametags are now " + (enhancedNametags ? "enabled." : "disabled.")));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightThroughBlocks(CommandContext<FabricClientCommandSource> context)
	{
		boolean highlight = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().highlightThroughWalls = highlight;
		FriendHighlighter.sendMessage(Text.literal("Friends will " + (highlight ? "be" : "not be") + " highlighted if blocks are in the way."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightWhileSneaking(CommandContext<FabricClientCommandSource> context)
	{
		boolean highlight = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().highlightWhileSneaking = highlight;
		FriendHighlighter.sendMessage(Text.literal("Players will " + (highlight ? "be" : "not be") + " highlighted if they are sneaking/crouched."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setRespectTeamColors(CommandContext<FabricClientCommandSource> context)
	{
		boolean respect = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().ignoreTeamColor = respect;
		FriendHighlighter.sendMessage(Text.literal(!respect ? "Friends will be highlighted by their team color if applicable." : "The set color will override friends' team color."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightProjectiles(CommandContext<FabricClientCommandSource> context)
	{
		boolean highlight = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().highlightProjectiles = highlight;
		FriendHighlighter.sendMessage(Text.literal(highlight ? "Projectiles will be highlighted the same colour as their thrower/shooter." : "Projectiles will be highlighted independently of their thrower."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightMobsYouHit(CommandContext<FabricClientCommandSource> context)
	{
		boolean highlight = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().highlightMobsYouHit = highlight;
		FriendHighlighter.sendMessage(Text.literal(highlight ? "Entities you hit will now stay highlighted for a period of time." : "Hitting an entity will not highlight them."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHitHighlightColor(CommandContext<FabricClientCommandSource> context)
	{
		String color = CommandUtils.getArgumentFromContext(context, VALUE, "#FFFF00");
		FHSettings.getSettings().hitHighlightColor = FHUtils.hexToRGB(color);;
		FriendHighlighter.sendMessage(Text.literal("").append("When hit, mobs will now be highlighted ").append(FHUtils.colorText(color, FHUtils.hexToRGB(color))));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHitHighlightSeconds(CommandContext<FabricClientCommandSource> context)
	{
		int seconds = CommandUtils.getArgumentFromContext(context, VALUE, 3);
		FHSettings.getSettings().hitHighlightSeconds = seconds;
		FriendHighlighter.sendMessage(Text.literal("").append("When hit, mobs will now be highlighted for " + seconds + " seconds."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}

	private int setHighlightAllEntities(CommandContext<FabricClientCommandSource> context)
	{
		boolean highlight = CommandUtils.getArgumentFromContext(context, VALUE, false);
		FHSettings.getSettings().highlightAllEntities = highlight;
		FriendHighlighter.sendMessage(Text.literal(highlight ? "All entities will be highlighted." : "Entities will be highlighted according to your lists."));
		cmdHandler.settingsChatMsg.updateContent();
		return com.mojang.brigadier.Command.SINGLE_SUCCESS;
	}
}
