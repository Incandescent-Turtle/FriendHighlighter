package mod.icy_turtle.friendhighlighter.config;

import com.google.common.collect.BiMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.FHColor;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import mod.icy_turtle.friendhighlighter.util.MultiPartGUIElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.EntityList;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            ConfigCategory friendsList = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.friendsList"));
            friendsList.addEntry(createFriendsList(entryBuilder));

            ConfigCategory modSettings = builder.getOrCreateCategory(Text.literal("Mod Settings"));
            createModSettings(modSettings, entryBuilder);

            ConfigCategory thing = builder.getOrCreateCategory(Text.literal("thing"));
            createThing(thing, entryBuilder);

            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateLists();
            });
            return builder.setParentScreen(screen).build();
        };
    }

    public static class HighlightedEntity
    {
        public int color = 0xFFFFFF;
        public String name = "test";
        public boolean enabled = true;
    }

    public static List<HighlightedEntity> highlightedEntityList = new ArrayList<>();

    private void createThing(ConfigCategory testing, ConfigEntryBuilder entryBuilder)
    {
        //SPAWN_EGGS?

        Map<String, EntityType<?>> entityMap = new HashMap<>();
        Iterable<EntityType<?>> entities = Registries.ENTITY_TYPE;

        for (EntityType<?> entityType : entities) {
            var s = getNameFromEntityType(entityType);
           entityMap.put(s.getString(), entityType);
        }
        for(var str : entityMap.keySet())
        {
//            testing.addEntry(entryBuilder.startBooleanToggle(Text.of(str), false).build());
            testing.addEntry(new MultiPartGUIElement(FHUtils.colorText(str, Formatting.RED.getColorValue()), entityMap.get(str), Arrays.asList(
                    entryBuilder.startColorField(Text.literal("Color"), 0xFFFFFF)
                            .setSaveConsumer(color -> {})
                            .build(),
                    entryBuilder.startBooleanToggle(Text.literal("Enabled"), true)
                            .setSaveConsumer((b) -> {})
                            .setTooltipSupplier(createToolTip(""))
                            .build()
            ), false));
        }
//        testing.addEntry(new NestedListListEntry<HighlightedEntity, MultiElementListEntry<HighlightedEntity>>(
//                Text.literal("Entity List"),
//                highlightedEntityList, // initial
//                true,
//                Optional::empty, //  tool tip
//                list -> {highlightedEntityList = list;}, // save
//                () -> highlightedEntityList, // default
//                entryBuilder.getResetButtonKey(),
//                true,
//                true,
//                (entityIn, nestedListListEntry) -> {
//                    final var entity = entityIn == null ? new HighlightedEntity() : entityIn;
//                    return new MultiElementListEntry<>(
//                            FHUtils.colorText(entity.name, entity.color),
//                            entity,
//                            Arrays.asList(
//                                    entryBuilder.startDropdownMenu(
//                                                    Text.literal("Mob Drop Down2"),
//                                                    DropdownMenuBuilder.TopCellElementBuilder.of(entityMap.values().stream().toList().get(0), entityMap::get, ModMenuIntegration::getNameFromEntityType),
//                                                    DropdownMenuBuilder.CellCreatorBuilder.of(20,100, 5, ModMenuIntegration::getNameFromEntityType)
//                                            ).setSelections(entityMap.values())
//                                            .setDefaultValue((Supplier<EntityType<? extends Entity>>) null)
//                                            .setSaveConsumer(e -> entity.name = e.getName().getString())
//                                            .build(),
//                                    entryBuilder.startColorField(Text.literal("Color"), entity.color)
//                                            .setSaveConsumer(color -> entity.color = color)
//                                            .build(),
//                                    entryBuilder.startBooleanToggle(Text.literal("Enabled"), entity.enabled)
//                                            .setSaveConsumer((b) -> {entity.enabled = true;})
//                                            .setTooltipSupplier(createToolTip(""))
//                                            .build()),
//                            entity.name.equals(""));
//                }
//        ));



        //        testing.addEntry(
//                entryBuilder.startDropdownMenu(
//                        Text.literal("Mob Drop Down"),
//                        DropdownMenuBuilder.TopCellElementBuilder.of(new FakeMob("dog"), FakeMob::createFromString, FakeMob::toText),
//                        DropdownMenuBuilder.CellCreatorBuilder.of(20,20, 5, FakeMob::toText)
//                ).setSelections(Arrays.stream("bob john henry harrison marge lemon".split(" ")).map(s -> new FakeMob(s)).collect(Collectors.toList()))
//                        .build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(Text.literal("Colors")).setExpanded(true);
        colors.add(entryBuilder.startDropdownMenu(
                Text.literal("lol apple"),
                DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE),
                DropdownMenuBuilder.CellCreatorBuilder.ofItemObject())
                .setDefaultValue(Items.APPLE)
                .setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new)))
                .setSaveConsumer(item -> System.out.println("save this " + item))
                .build());
        testing.addEntry(colors.build());

        testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {

                    }
                    return null;
                })).setDefaultValue(10).setSelections(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).build());
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
                list -> FriendsListHandler.setFriendsMap(playerListToMap(list)),
                () -> mapToFriendsList(FriendsListHandler.getFriendsMap()),
                entryBuilder.getResetButtonKey(),
                true,
                true,
                (friendIn, nestedListListEntry) -> {
                    final var friend = friendIn == null ? new HighlightedFriend() : friendIn;
                    return new MultiPartGUIElement<>(
                            Text.literal("").append(FHUtils.colorText(friend.name, friend.color) ).append(FHUtils.getMessageWithConnotation(" ✓", " ✖", friend.isEnabled())), friend,
                            Arrays.asList(
                                    entryBuilder.startTextField(Text.literal("Name"), friend.name)
                                            .setSaveConsumer(str -> friend.name = str)
                                            .setErrorSupplier(str -> str.equals("") ? Optional.of(Text.of("Friend Name cannot be blank.")) : Optional.empty())
                                            .build(),
                                    entryBuilder.startColorField(Text.literal("Color"), friend.color)
                                            .setSaveConsumer(color -> friend.color = color)
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Only Players"), friend.onlyPlayers)
                                            .setSaveConsumer(onlyPlayers -> friend.onlyPlayers = onlyPlayers)
                                            .setTooltipSupplier(createToolTip("Whether only player's with this name will get highlighted."))
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Just Nametag"), friend.justNameTag)
                                            .setSaveConsumer(justNametag -> friend.justNameTag = justNametag)
                                            .setTooltipSupplier(createToolTip("Whether " + (friend.onlyPlayers ? "players" : "entities") + " with this name will be outlined in addition their name tag always showing and being colored."))
                                            .build(),
                                    entryBuilder.startBooleanToggle(Text.literal("Enabled"), friend.isEnabled())
                                            .setSaveConsumer(friend::setEnabled)
                                            .setTooltipSupplier(createToolTip("Toggles whether this friend will currently be highlighted and have its name colored."))
                                            .build()),
                            friend.name.equals(""));
                }
        );
    }

    /**
     * Populates the modConfig category to hold config settings for the mod.
     * @param settingsCategory the config category.
     * @param entryBuilder the entry builder.
     */
    private void createModSettings(ConfigCategory settingsCategory, ConfigEntryBuilder entryBuilder)
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
    private static LinkedHashMap<String, HighlightedFriend> playerListToMap(List<HighlightedFriend> list)
    {
        LinkedHashMap<String, HighlightedFriend> map = new LinkedHashMap<>();
        //  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
        Collections.reverse(list);
        list.forEach(p -> map.put(p.name, p));
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

    private static Text getNameFromEntityType(EntityType<?> type)
    {
        return Text.of(type.getName().getString());
    }
}