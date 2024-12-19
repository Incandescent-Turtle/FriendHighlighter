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
		final Map<String, EntityType<?>> entityTypeMap = getEntityTypeMap();

		// Building the GUI components for each mob
		final var highlightedEntityMap = FriendsListHandler.getEntityMap();
		for(final var entry : entityTypeMap.entrySet())
		{
			final var name = entry.getKey();
			final var type = entry.getValue();

			HighlightedEntity highlightedEntity;

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
			var enabledField = entryBuilder.startBooleanToggle(Text.literal("Enabled"), isNew ? false : true)
					.setSaveConsumer((enabled) ->
					{
						var map = FriendsListHandler.getEntityMap();
						var inEntityMap = map.containsKey(name);

						if(enabled)
						{
							if(!inEntityMap)
							{
								map.put(name, highlightedEntity);
							}
						} else if(inEntityMap){
							map.remove(name);
						}
					})
					.build();

			var colorField = entryBuilder.startColorField(Text.literal("Color"), highlightedEntity.getColor())
					.setSaveConsumer(color -> highlightedEntity.setColor(color))
					.setDefaultValue(highlightedEntity.getColor())
					.build();

			category.addEntry(new MultiPartGUIElement(
					FHUtils.colorText(name, highlightedEntity.getColor()),
					entityTypeMap.get(name),
					Arrays.asList(colorField, enabledField),
					false)
			);
		}
	}

	private static Map<String, EntityType<?>> getEntityTypeMap()
	{
		final Map<String, EntityType<?>> entityTypeMap = new HashMap<>();
		for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
			var s = FHUtils.getNameFromEntityType(entityType).getString();
			entityTypeMap.put(s, entityType);
		}
		return entityTypeMap;
	}
}
