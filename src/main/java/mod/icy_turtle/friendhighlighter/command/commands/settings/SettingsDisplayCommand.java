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
}
