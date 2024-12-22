package mod.icy_turtle.friendhighlighter.gui;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.text.Text;

public class ModSettingsPage
{
	/**
	 * Populates the modConfig category to hold config settings for the mod.
	 * @param settingsCategory the config category.
	 * @param entryBuilder the entry builder.
	 */
	public static void addModSettingsToCategory(ConfigCategory settingsCategory, ConfigEntryBuilder entryBuilder)
	{
		var settings = FHSettings.getSettings();
		settingsCategory.addEntry(
				entryBuilder.startEnumSelector(Text.literal("Message Display Method"), FHSettings.MessageDisplayMethod.class, settings.messageDisplayMethod)
						.setSaveConsumer(displayMethod -> settings.messageDisplayMethod = displayMethod)
						.setEnumNameProvider(displayMethod ->  Text.literal(FHUtils.capitalizeAllFirstLetters(displayMethod.name().replaceAll("_", " "))))
						.setTooltipSupplier(GuiUtil.createToolTip("How a message informing you of a change to your friends list is displayed. As a chat message, above the hotbar, or both."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startColorField(Text.literal("Default Color"), settings.defaultColor)
						.setSaveConsumer(color -> settings.defaultColor = color)
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Highlight Only Players by Default"), settings.defaultPlayersOnly)
						.setSaveConsumer(onlyPlayers -> FHSettings.getSettings().defaultPlayersOnly = onlyPlayers)
						.setTooltipSupplier(GuiUtil.createToolTip("When using commands to add a friend, you can select whether you want to highlight only players of that name or all mobs. If you don't specify, this default value will be given to that friend."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Show Tooltips"), FHSettings.getSettings().tooltipsEnabled)
						.setSaveConsumer(show -> FHSettings.getSettings().tooltipsEnabled = show)
						.setTooltipSupplier(GuiUtil.createToolTip("Whether tooltips (like this) should be displayed in the chat interface and this GUI."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Highlight when invisible"), FHSettings.getSettings().highlightInvisibleFriends)
						.setSaveConsumer(highlight -> FHSettings.getSettings().highlightInvisibleFriends = highlight)
						.setTooltipSupplier(GuiUtil.createToolTip("Whether friends get highlighted when they are invisible. Also applies to nametag rendering/colouring when invisible."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Enhanced Nametags"), FHSettings.getSettings().enhancedNametags)
						.setSaveConsumer(enhanced -> FHSettings.getSettings().enhancedNametags = enhanced)
						.setTooltipSupplier(GuiUtil.createToolTip("When enabled, nametags will render more clearly through blocks so you can read the name better."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Highlight Through Walls"), FHSettings.getSettings().highlightThroughWalls)
						.setSaveConsumer(highlight -> FHSettings.getSettings().highlightThroughWalls = highlight)
						.setTooltipSupplier(GuiUtil.createToolTip("When enabled, friends will be highlighted even when you cannot see them (aka when they are behind blocks)."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Highlight While Sneaking"), FHSettings.getSettings().highlightWhileSneaking)
						.setSaveConsumer(highlight -> FHSettings.getSettings().highlightWhileSneaking = highlight)
						.setTooltipSupplier(GuiUtil.createToolTip("When enabled, players will be highlighted even when they are sneaking/crouched."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Ignore Team Colors"), FHSettings.getSettings().ignoreTeamColor)
						.setSaveConsumer(respect -> FHSettings.getSettings().ignoreTeamColor = respect)
						.setTooltipSupplier(GuiUtil.createToolTip("When disabled, if a friend is on a team, their team color will be used instead of the set color."))
						.build()
		);
		settingsCategory.addEntry(
				entryBuilder.startBooleanToggle(Text.literal("Highlight Friend/Entity Projectiles"), FHSettings.getSettings().highlightProjectiles)
						.setSaveConsumer(highlight -> FHSettings.getSettings().highlightProjectiles = highlight)
						.setTooltipSupplier(GuiUtil.createToolTip("When enabled, if a friend/entity is highlighted, any projectiles they throw/shot by them will be highlighted with the same colour."))
						.build()
		);
	}
}
