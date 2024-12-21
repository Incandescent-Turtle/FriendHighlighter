package mod.icy_turtle.friendhighlighter.gui;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedEntity;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import mod.icy_turtle.friendhighlighter.util.MultiPartGUIElement;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntitySelectPage
{

	public static void addEntitySelectionElements(ConfigCategory category, ConfigEntryBuilder entryBuilder)
	{
		// Loading all entities into a map
		final Map<String, EntityType<?>> entityTypeMap = FHUtils.getEntityTypeMap();

		// Entities pulled from the config (entities that have had their color changed, may be enabled or disabled)
		final var highlightedEntityMap = FriendsListHandler.getEntityMap();

		// Building the GUI components for each mob in the game
		for(final var entry : entityTypeMap.entrySet())
		{
			final var name = entry.getKey();
			final var type = entry.getValue();

			HighlightedEntity highlightedEntity;

			// Whether this mob exists in the config yet
			boolean isNew = false;
			// if this entity is already registered, pull that entry
			if(highlightedEntityMap.containsKey(name))
			{
				highlightedEntity = highlightedEntityMap.get(name);
			} else {
				highlightedEntity = new HighlightedEntity(type);
				isNew = true;
			}
			// if enabled, make sure it gets stored in our map
			// otherwise, make sure it isnt in our map
			var enabledField = entryBuilder.startBooleanToggle(Text.literal("Enabled"), highlightedEntity.isEnabled())
					.setSaveConsumer((enabled) ->
					{
						var map = FriendsListHandler.getEntityMap();

						if(highlightedEntity.isEnabled() != enabled)
						{
							map.put(name, highlightedEntity);
						}

						highlightedEntity.setEnabled(enabled);

					})
					.build();

			var colorField = entryBuilder.startColorField(Text.literal("Color"), highlightedEntity.getColor())
					.setSaveConsumer(color ->{
						var map = FriendsListHandler.getEntityMap();

						if(highlightedEntity.getColor() != color)
						{
							map.put(name, highlightedEntity);
						}

						highlightedEntity.setColor(color);
					})
					.setDefaultValue(highlightedEntity.getColor())
					.build();

			var indicator = FHUtils.getMessageWithConnotation(GuiUtil.INDICATOR_SQUARE, highlightedEntity.isEnabled());
			category.addEntry(new MultiPartGUIElement(
					Text.literal("").append(FHUtils.colorText(name, highlightedEntity.getColor())).append(" ").append(indicator),
					entityTypeMap.get(name),
					Arrays.asList(colorField, enabledField),
					false)
			);
		}
	}
}
