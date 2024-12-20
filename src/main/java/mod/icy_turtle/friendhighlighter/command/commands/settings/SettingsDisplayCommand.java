package mod.icy_turtle.friendhighlighter.command.commands.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SettingsDisplayCommand extends Command
{
	public SettingsDisplayCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("display")
				.executes(context -> cmdHandler.settingsChatMsg.sendInChat());
	}

	public static MutableText createSettings()
	{
		var settings = FHSettings.getSettings();

		var title = Text.literal("Friend Highlighter Settings").styled(style -> style.withBold(true).withUnderline(true));

		var txt = Text.literal("");
		txt.append(title);
		txt.append("\n\n");
		txt.append(createDisplayMethodText(settings));
		txt.append("\n\n");
		txt.append(createTooltipText(settings));
		txt.append("\n\n");
		txt.append(createHighlightInvisibleFriendsText(settings));
		txt.append("\n\n");
		txt.append(createDefaultPlayersOnlyText(settings));
		txt.append("\n\n");
		txt.append(createEnhancedNametagsText(settings));
		txt.append("\n\n");
		txt.append(createHighlightThroughBlocksText(settings));
		txt.append("\n\n");
		txt.append(createHighlightWhileSneakingText(settings));
		txt.append("\n\n");
		txt.append(createIgnoreTeamColorText(settings));
		return txt;
	}

	private static MutableText createDisplayMethodText(FHSettings settings)
	{
		var title = FHUtils.colorText("Message Display Method", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Change the way updates to your friends list appear");

		var methods = FHSettings.MessageDisplayMethod.values();
		var methodChoiceText = Text.literal("");
		for(int i = 0; i < methods.length; i++)
		{
			var methodName = methods[i].name();
			var isSelected = methodName.equals(settings.messageDisplayMethod.name());
			var methodText = Text.literal(methodName);
			CommandUtils.addHoverAndClickEvent(
					methodText.styled(style -> style.withBold(isSelected).withColor(isSelected ? Formatting.GREEN : Formatting.RED)),
					"PLACE HOLDER",
					"/fh settings set messageDisplayMethod " + methodName
			);
			methodChoiceText.append(methodText);
			if(i < methods.length-1)
			{
				methodChoiceText.append(" | ");
			}
		}

		var mdText = Text.literal("");
		mdText.append(title);
		mdText.append("\n ↳ ");
		mdText.append(methodChoiceText);
		return mdText;
	}

	private static MutableText createTooltipText(FHSettings settings)
	{
		var title = FHUtils.colorText("Tooltip Visibility", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Toggle visibility of tooltips in the chat and other menus");

		var enabled = settings.tooltipsEnabled;
		var ttVisible = CommandUtils.addHoverAndClickEvent(
				Text.literal("Visible").styled(style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED).withBold(enabled)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set tooltipVisibility visible"
		);

		var ttHidden = CommandUtils.addHoverAndClickEvent(
				Text.literal("Hidden").styled(style -> style.withColor(enabled ? Formatting.RED : Formatting.GREEN).withBold(!enabled)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set tooltipVisibility hidden"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(ttVisible);
		tooltipText.append(" | ");
		tooltipText.append(ttHidden);
		return tooltipText;
	}

	private static MutableText createHighlightInvisibleFriendsText(FHSettings settings)
	{
		var title = FHUtils.colorText("Invisible Friends", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Toggle whether friends will be highlighted when they have the invisibility effect");

		var enabled = settings.highlightInvisibleFriends;
		var visible = CommandUtils.addHoverAndClickEvent(
				Text.literal("Highlight").styled(style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED).withBold(enabled)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightInvisibleFriends enabled"
		);

		var hidden = CommandUtils.addHoverAndClickEvent(
				Text.literal("Ignore").styled(style -> style.withColor(enabled ? Formatting.RED : Formatting.GREEN).withBold(!enabled)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightInvisibleFriends disabled"
		);

		var text = Text.literal("");
		text.append(title);
		text.append("\n ↳ ");
		text.append(visible);
		text.append(" | ");
		text.append(hidden);
		return text;
	}

	private static MutableText createDefaultPlayersOnlyText(FHSettings settings) {
		var title = FHUtils.colorText("Highlight Players Only by Default", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Whether new friend entries will highlight just players by default or all entities");

		var onlyPlayers = settings.defaultPlayersOnly;
		var players = CommandUtils.addHoverAndClickEvent(
				Text.literal("Only Players").styled(style -> style.withColor(onlyPlayers ? Formatting.GREEN : Formatting.RED).withBold(onlyPlayers)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set defaultPlayersOnly onlyPlayers"
		);

		var entities = CommandUtils.addHoverAndClickEvent(
				Text.literal("All Entities").styled(style -> style.withColor(onlyPlayers ? Formatting.RED : Formatting.GREEN).withBold(!onlyPlayers)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set defaultPlayersOnly allEntities"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(players);
		tooltipText.append(" | ");
		tooltipText.append(entities);
		return tooltipText;
	}

	private static MutableText createEnhancedNametagsText(FHSettings settings) {
		var title = FHUtils.colorText("Enhanced Nametags", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Allow nametags to appear through blocks clearer");

		var status = settings.enhancedNametags;
		var enhanced = CommandUtils.addHoverAndClickEvent(
				Text.literal("Enhanced").styled(style -> style.withColor(status ? Formatting.GREEN : Formatting.RED).withBold(status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set enhancedNametags enhanced"
		);

		var normal = CommandUtils.addHoverAndClickEvent(
				Text.literal("Normal").styled(style -> style.withColor(status ? Formatting.RED : Formatting.GREEN).withBold(!status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set enhancedNametags normal"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(enhanced);
		tooltipText.append(" | ");
		tooltipText.append(normal);
		return tooltipText;
	}

	private static MutableText createHighlightThroughBlocksText(FHSettings settings) {
		var title = FHUtils.colorText("Highlight Through Blocks", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Toggles highlighting friends through blocks");

		var status = settings.highlightThroughWalls;
		var highlight = CommandUtils.addHoverAndClickEvent(
				Text.literal("Highlight").styled(style -> style.withColor(status ? Formatting.GREEN : Formatting.RED).withBold(status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightThroughBlocks highlight"
		);

		var dontHighlight = CommandUtils.addHoverAndClickEvent(
				Text.literal("Don't Highlight").styled(style -> style.withColor(status ? Formatting.RED : Formatting.GREEN).withBold(!status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightThroughBlocks dontHighlight"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(highlight);
		tooltipText.append(" | ");
		tooltipText.append(dontHighlight);
		return tooltipText;
	}

	private static MutableText createHighlightWhileSneakingText(FHSettings settings) {
		var title = FHUtils.colorText("Highlight While Sneaking", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Toggles highlighting players while sneaking");

		var status = settings.highlightWhileSneaking;
		var highlight = CommandUtils.addHoverAndClickEvent(
				Text.literal("Highlight").styled(style -> style.withColor(status ? Formatting.GREEN : Formatting.RED).withBold(status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightWhileSneaking highlight"
		);

		var dontHighlight = CommandUtils.addHoverAndClickEvent(
				Text.literal("Don't Highlight").styled(style -> style.withColor(status ? Formatting.RED : Formatting.GREEN).withBold(!status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set highlightWhileSneaking dontHighlight"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(highlight);
		tooltipText.append(" | ");
		tooltipText.append(dontHighlight);
		return tooltipText;
	}

	private static MutableText createIgnoreTeamColorText(FHSettings settings) {
		var title = FHUtils.colorText("Ignore Team Color", Color.ORANGE.getRGB());
		CommandUtils.addToolTip(title, "Toggles ignoring the team color of friends");

		var status = settings.ignoreTeamColor;
		var ignore = CommandUtils.addHoverAndClickEvent(
				Text.literal("Ignore").styled(style -> style.withColor(status ? Formatting.GREEN : Formatting.RED).withBold(status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set ignoreTeamColor ignoreTeamColor"
		);

		var respect = CommandUtils.addHoverAndClickEvent(
				Text.literal("Respect").styled(style -> style.withColor(status ? Formatting.RED : Formatting.GREEN).withBold(!status)),
				"PLACEHOLDER - LINK VIA LANG",
				"/fh settings set ignoreTeamColor useTeamColor"
		);

		var tooltipText = Text.literal("");
		tooltipText.append(title);
		tooltipText.append("\n ↳ ");
		tooltipText.append(ignore);
		tooltipText.append(" | ");
		tooltipText.append(respect);
		return tooltipText;
	}
}
