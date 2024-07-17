package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import mod.icy_turtle.friendhighlighter.util.MultiPartGUIElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Supplier;

/**
 * Integration for the ModMenu mod.
 */
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.translatable("config.friendHighlighter.title"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory friendsListCategory = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.friendsList"));
            friendsListCategory.addEntry(createFriendsList(entryBuilder));

            ConfigCategory modSettingsCategory = builder.getOrCreateCategory(Text.literal("Mod Settings"));
            addModSettingsToCategory(modSettingsCategory, entryBuilder);

            ConfigCategory entitySelectCategory = builder.getOrCreateCategory(Text.literal("Entity Selection Screen"));
            addEntitySelectionElements(entitySelectCategory, entryBuilder);

            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateLists();
            });
            return builder.setParentScreen(screen).build();
        };
    }

    private void addEntitySelectionElements(ConfigCategory category, ConfigEntryBuilder entryBuilder)
    {
        //SPAWN_EGGS?

        // Loading all entities into a map
        final Map<String, EntityType<?>> entityTypeMap = new HashMap<>();
        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            var s = FHUtils.getNameFromEntityType(entityType).getString();
           entityTypeMap.put(s, entityType);
        }

        // Building the GUI components for each mob
        final var highlightedEntityMap = FriendsListHandler.getEntityMap();
        for(final var name : entityTypeMap.keySet())
        {
            var type = entityTypeMap.get(name);
            HighlightedEntity highlightedEntity;

            boolean isNew = false;
            // if this entity is already highlighted, pull that entry
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
                        if(name.equals("Sheep"))
                        {
                            System.out.println("enabled: " + enabled);
                            System.out.println("inmap: " + inEntityMap);
                            System.out.println("map " + map);
                        }
                        if(enabled)
                        {
                            if(!inEntityMap)
                            {
                                map.put(name, highlightedEntity);
                                System.out.println("putting " + name + " in");
                                System.out.println("current: " + highlightedEntityMap);
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

            category.addEntry(new MultiPartGUIElement(FHUtils.colorText(name, Formatting.RED.getColorValue()), entityTypeMap.get(name), Arrays.asList(colorField, enabledField), false));
        }
    }

    /**
     * Create the list entry for configuring the friends list.
     * @param entryBuilder the entry builder.
     * @return returns the {@link NestedListListEntry}.
     */
    private AbstractConfigListEntry createFriendsList(ConfigEntryBuilder entryBuilder)
    {
        return new NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>>(
                Text.literal("Friend's List"),
                mapToFriendsList(FriendsListHandler.getFriendsMap()), // initial
                true,
                Optional::empty, //  tool tip
                list -> FriendsListHandler.setFriendsMap(friendListToMap(list)),
                () -> mapToFriendsList(FriendsListHandler.getFriendsMap()),
                entryBuilder.getResetButtonKey(),
                true,
                true,
                (friendIn, nestedListListEntry) -> {
                    final var friend = friendIn == null ? new HighlightedFriend() : friendIn;
                    return new MultiPartGUIElement<>(
                            Text.literal("").append(FHUtils.colorText(friend.getName(), friend.getColor()) ).append(FHUtils.getMessageWithConnotation(" ✓", " ✖", friend.isEnabled())), friend,
                            Arrays.asList(
                                    entryBuilder.startTextField(Text.literal("Name"), friend.getName())
                                            .setSaveConsumer(str -> friend.setName(str))
                                            .setErrorSupplier(str -> str.equals("") ? Optional.of(Text.of("Friend Name cannot be blank.")) : Optional.empty())
                                            .build(),
                                    entryBuilder.startColorField(Text.literal("Color"), friend.getColor())
                                            .setSaveConsumer(color -> friend.setColor(color))
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Only Players"), friend.isOnlyPlayers())
                                            .setSaveConsumer(onlyPlayers -> friend.setOnlyPlayers(onlyPlayers))
                                            .setTooltipSupplier(createToolTip("Whether only player's with this name will get highlighted."))
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Just Nametag"), friend.isJustNameTag())
                                            .setSaveConsumer(justNametag -> friend.setJustNameTag(justNametag))
                                            .setTooltipSupplier(createToolTip("Whether " + (friend.isOnlyPlayers() ? "players" : "entities") + " with this name will be outlined in addition their name tag always showing and being colored."))
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Enabled"), friend.isEnabled())
                                            .setSaveConsumer(friend::setEnabled)
                                            .setTooltipSupplier(createToolTip("Toggles whether this friend will currently be highlighted and have its name colored."))
                                            .build()),
                            friend.getName().equals(""));
                }
        );
    }

    /**
     * Populates the modConfig category to hold config settings for the mod.
     * @param settingsCategory the config category.
     * @param entryBuilder the entry builder.
     */
    private void addModSettingsToCategory(ConfigCategory settingsCategory, ConfigEntryBuilder entryBuilder)
    {
        var settings = FHSettings.getSettings();
        settingsCategory.addEntry(
                entryBuilder.startEnumSelector(Text.literal("Message Display Method"), FHSettings.MessageDisplayMethod.class, settings.messageDisplayMethod)
                        .setSaveConsumer(displayMethod -> settings.messageDisplayMethod = displayMethod)
                        .setEnumNameProvider(displayMethod ->  Text.literal(FHUtils.capitalizeAllFirstLetters(displayMethod.name().replaceAll("_", " "))))
                        .setTooltipSupplier(createToolTip("How a message informing you of a change to your friends list is displayed. As a chat message, above the hotbar, or both."))
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
                        .setTooltipSupplier(createToolTip("When using commands to add a friend, you can select whether you want to highlight only players of that name or all mobs. If you don't specify, this default value will be given to that friend."))
                        .build()
        );
        settingsCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Show Tooltips"), FHSettings.getSettings().tooltipsEnabled)
                        .setSaveConsumer(show -> FHSettings.getSettings().tooltipsEnabled = show)
                        .setTooltipSupplier(createToolTip("Whether tooltips (like this) should be displayed in the chat interface and this GUI."))
                        .build()
        );
        settingsCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Highlight when invisible"), FHSettings.getSettings().highlightInvisibleFriends)
                        .setSaveConsumer(highlight -> FHSettings.getSettings().highlightInvisibleFriends = highlight)
                        .setTooltipSupplier(createToolTip("Whether friends get highlighted when they are invisible. Also applies to nametag rendering/colouring when invisible."))
                        .build()
        );
        settingsCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Enhanced Nametags"), FHSettings.getSettings().enhancedNametags)
                        .setSaveConsumer(enhanced -> FHSettings.getSettings().enhancedNametags = enhanced)
                        .setTooltipSupplier(createToolTip("When enabled, nametags will render more clearly through blocks so you can read the name better."))
                        .build()
        );
        settingsCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Highlight Through Walls"), FHSettings.getSettings().highlightThroughWalls)
                        .setSaveConsumer(highlight -> FHSettings.getSettings().highlightThroughWalls = highlight)
                        .setTooltipSupplier(createToolTip("When enabled, friends will be highlighted even when you cannot see them (aka when they are behind blocks)."))
                        .build()
        );
        settingsCategory.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Highlight While Sneaking"), FHSettings.getSettings().highlightWhileSneaking)
                        .setSaveConsumer(highlight -> FHSettings.getSettings().highlightWhileSneaking = highlight)
                        .setTooltipSupplier(createToolTip("When enabled, players will be highlighted even when they are sneaking/crouched."))
                        .build()
        );
    }

    /**
     * Converts the given list of {@link HighlightedFriend}s to a map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
     * @param list the friends list to convert.
     * @return the map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
     */
    private static LinkedHashMap<String, HighlightedFriend> friendListToMap(List<HighlightedFriend> list)
    {
        LinkedHashMap<String, HighlightedFriend> map = new LinkedHashMap<>();
        //  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
        Collections.reverse(list);
        list.forEach(f -> map.put(f.getName(), f));
        return map;
    }

    /**
     * Converts the given map to a list of friends.
     * @param map a map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
     * @return the map,
     */
    private static List<HighlightedFriend> mapToFriendsList(LinkedHashMap<String, HighlightedFriend> map)
    {
        var list = new ArrayList<>(map.values());
        //  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
        Collections.reverse(list);
        return list;
    }

    private Supplier<Optional<Text[]>> createToolTip(String str)
    {
        if(FHSettings.getSettings().tooltipsEnabled)
            return () -> Optional.of(new Text[]{Text.literal(FHUtils.splitEveryNCharacters(str, 20))});
        return Optional::empty;
    }
}