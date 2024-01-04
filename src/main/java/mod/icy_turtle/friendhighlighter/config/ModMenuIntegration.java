package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

//            ConfigCategory thing = builder.getOrCreateCategory(Text.literal("thing"));
//            createThing(thing, entryBuilder);

            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateLists();
            });
            return builder.setParentScreen(screen).build();
        };
    }

    static public class FakeMob
    {
        private String name;

        public FakeMob(String name)
        {
            this.name = name;
        }
        public static FakeMob createFromString(String str)
        {
            return new FakeMob(str);
        }

        public static Text toText(FakeMob mob)
        {
            return Text.literal(mob.name).styled(s -> s.withBold(true).withColor(Formatting.RED));
        }
    }

    private void createThing(ConfigCategory testing, ConfigEntryBuilder entryBuilder)
    {
        //SPAWN_EGGS
        testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Mob Drop Down"), DropdownMenuBuilder.TopCellElementBuilder.of(new FakeMob("dog"), FakeMob::createFromString, FakeMob::toText), DropdownMenuBuilder.CellCreatorBuilder.of(100,100, 5, FakeMob::toText)).setSelections(Arrays.stream("bob john henry harrison marge lemon".split(" ")).map(s -> new FakeMob(s)).collect(Collectors.toList())).build());
        testing.addEntry(entryBuilder.startKeyCodeField(Text.literal("Cool Key"), InputUtil.UNKNOWN_KEY).setDefaultValue(InputUtil.UNKNOWN_KEY).build());

        testing.addEntry(entryBuilder.startModifierKeyCodeField(Text.literal("Cool Modifier Key"), ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(79), Modifier.of(false, true, false))).build());
        SubCategoryBuilder colors = entryBuilder.startSubCategory(Text.literal("Colors")).setExpanded(true);
        colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        SubCategoryBuilder innerColors = entryBuilder.startSubCategory(Text.literal("Inner Colors")).setExpanded(true);
        innerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
        colors.add(innerColors.build());
        testing.addEntry(colors.build());
        testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {

                    }
                    return null;
                })).setDefaultValue(10).setSelections(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).build());
        testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {

                    }
                    return null;
                })).setDefaultValue(5).setSuggestionMode(false).setSelections(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))).build());
        testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                Text.literal("Nice"),
                new ArrayList<>(List.of(new Pair<>(10, 10), new Pair<>(20, 40))),
                false,
                Optional::empty,
                list -> {},
                () -> new ArrayList<>(List.of(new Pair<>(10, 10), new Pair<>(20, 40))),
                entryBuilder.getResetButtonKey(),
                true,
                true,
                (elem, nestedListListEntry) -> {
                    if (elem == null) {
                        Pair<Integer, Integer> newDefaultElemValue = new Pair<>(10, 10);
                        return new MultiElementListEntry<>(Text.literal("Pair"), newDefaultElemValue,
                                new ArrayList<>(List.of(entryBuilder.startIntField(Text.literal("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(Text.literal("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build())),
                                true);
                    } else {
                        return new MultiElementListEntry<>(Text.literal("Pair"), elem,
                                new ArrayList<>(List.of(entryBuilder.startIntField(Text.literal("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                        entryBuilder.startIntField(Text.literal("Right"), elem.getRight()).setDefaultValue(10).build())),
                                true);
                    }
                }
        ));
        testing.addEntry(entryBuilder.startTextDescription(
                Text.translatable("text.cloth-config.testing.1",
                        Text.literal("ClothConfig").styled(s -> s.withBold(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(Util.make(new ItemStack(Items.PINK_WOOL), stack -> stack.setCustomName(Text.literal("(\u30FB\u2200\u30FB)")).addEnchantment(Enchantments.EFFICIENCY, 10)))))),
                        Text.translatable("text.cloth-config.testing.2").styled(s -> s.withColor(Formatting.BLUE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("https://shedaniel.gitbook.io/cloth-config/"))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://shedaniel.gitbook.io/cloth-config/"))),
                        Text.translatable("text.cloth-config.testing.3").styled(s -> s.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Utils.getConfigFolder().getParent().resolve("options.txt").toString())))
                )
        ).build());
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
                    return new MultiElementListEntry<>(
                            FHUtils.getMessageWithConnotation(friend.name.equals("") ? "Friend" : friend.name, friend.isEnabled()), friend,
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

    /*


    public class ClothConfigDemo {
        public static ConfigBuilder getConfigBuilderWithDemo() {
            class Pair<T, R> {
                final T t;
                final R r;

                public Pair(T t, R r) {
                    this.t = t;
                    this.r = r;
                }

                public T getLeft() {
                    return t;
                }

                public R getRight() {
                    return r;
                }
            }

            ConfigBuilder builder = ConfigBuilder.create().setTitle(Component.translatable("title.cloth-config.config"));
            builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
            builder.setGlobalized(true);
            builder.setGlobalizedExpanded(false);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory testing = builder.getOrCreateCategory(Component.translatable("category.cloth-config.testing"));
            testing.addEntry(entryBuilder.startKeyCodeField(Text.literal("Cool Key"), InputConstants.UNKNOWN).setDefaultValue(InputConstants.UNKNOWN).build());
            testing.addEntry(entryBuilder.startModifierKeyCodeField(Text.literal("Cool Modifier Key"), ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).setDefaultValue(ModifierKeyCode.of(InputConstants.Type.KEYSYM.getOrCreate(79), Modifier.of(false, true, false))).build());
            testing.addEntry(entryBuilder.startDoubleList(Text.literal("A list of Doubles"), Arrays.asList(1d, 2d, 3d)).setDefaultValue(Arrays.asList(1d, 2d, 3d)).build());
            testing.addEntry(entryBuilder.startLongList(Text.literal("A list of Longs"), Arrays.asList(1L, 2L, 3L)).setDefaultValue(Arrays.asList(1L, 2L, 3L)).setInsertButtonEnabled(false).build());
            testing.addEntry(entryBuilder.startStrList(Text.literal("A list of Strings"), Arrays.asList("abc", "xyz")).setTooltip(Text.literal("Yes this is some beautiful tooltip\nOh and this is the second line!")).setDefaultValue(Arrays.asList("abc", "xyz")).build());
            SubCategoryBuilder colors = entryBuilder.startSubCategory(Text.literal("Colors")).setExpanded(true);
            colors.add(entryBuilder.startColorField(Text.literal("A color field"), 0x00ffff).setDefaultValue(0x00ffff).build());
            colors.add(entryBuilder.startColorField(Text.literal("An alpha color field"), 0xff00ffff).setDefaultValue(0xff00ffff).setAlphaMode(true).build());
            colors.add(entryBuilder.startColorField(Text.literal("An alpha color field"), 0xffffffff).setDefaultValue(0xffff0000).setAlphaMode(true).build());
            colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            colors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            SubCategoryBuilder innerColors = entryBuilder.startSubCategory(Text.literal("Inner Colors")).setExpanded(true);
            innerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            innerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            innerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            SubCategoryBuilder innerInnerColors = entryBuilder.startSubCategory(Text.literal("Inner Inner Colors")).setExpanded(true);
            innerInnerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            innerInnerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            innerInnerColors.add(entryBuilder.startDropdownMenu(Text.literal("lol apple"), DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(Items.APPLE), DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()).setDefaultValue(Items.APPLE).setSelections(Registries.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new))).setSaveConsumer(item -> System.out.println("save this " + item)).build());
            innerColors.add(innerInnerColors.build());
            colors.add(innerColors.build());
            testing.addEntry(colors.build());
            testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                    s -> {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException ignored) {

                        }
                        return null;
                    })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
            testing.addEntry(entryBuilder.startDropdownMenu(Text.literal("Selection Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                    s -> {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException ignored) {

                        }
                        return null;
                    })).setDefaultValue(5).setSuggestionMode(false).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
            testing.addEntry(new NestedListListEntry<Pair<Integer, Integer>, MultiElementListEntry<Pair<Integer, Integer>>>(
                    Text.literal("Nice"),
                    Lists.newArrayList(new Pair<>(10, 10), new Pair<>(20, 40)),
                    false,
                    Optional::empty,
                    list -> {},
                    () -> Lists.newArrayList(new Pair<>(10, 10), new Pair<>(20, 40)),
                    entryBuilder.getResetButtonKey(),
                    true,
                    true,
                    (elem, nestedListListEntry) -> {
                        if (elem == null) {
                            Pair<Integer, Integer> newDefaultElemValue = new Pair<>(10, 10);
                            return new MultiElementListEntry<>(Text.literal("Pair"), newDefaultElemValue,
                                    Lists.newArrayList(entryBuilder.startIntField(Text.literal("Left"), newDefaultElemValue.getLeft()).setDefaultValue(10).build(),
                                            entryBuilder.startIntField(Text.literal("Right"), newDefaultElemValue.getRight()).setDefaultValue(10).build()),
                                    true);
                        } else {
                            return new MultiElementListEntry<>(Text.literal("Pair"), elem,
                                    Lists.newArrayList(entryBuilder.startIntField(Text.literal("Left"), elem.getLeft()).setDefaultValue(10).build(),
                                            entryBuilder.startIntField(Text.literal("Right"), elem.getRight()).setDefaultValue(10).build()),
                                    true);
                        }
                    }
            ));
            testing.addEntry(entryBuilder.startTextDescription(
                    Component.translatable("text.cloth-config.testing.1",
                            Text.literal("ClothConfig").withStyle(s -> s.withBold(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(Util.make(new ItemStack(Items.PINK_WOOL), stack -> stack.setHoverName(Text.literal("(\u30FB\u2200\u30FB)")).enchant(Enchantments.BLOCK_EFFICIENCY, 10)))))),
                            Component.translatable("text.cloth-config.testing.2").withStyle(s -> s.withColor(ChatFormatting.BLUE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("https://shedaniel.gitbook.io/cloth-config/"))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://shedaniel.gitbook.io/cloth-config/"))),
                            Component.translatable("text.cloth-config.testing.3").withStyle(s -> s.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Utils.getConfigFolder().getParent().resolve("options.txt").toString())))
                    )
            ).build());
            builder.transparentBackground();
            return builder;
        }
    }
     */

    static class Pair<T, R>
    {
        final T t;
        final R r;

        public Pair(T t, R r)
        {
            this.t = t;
            this.r = r;
        }

        public T getLeft()
        {
            return t;
        }

        public R getRight()
        {
            return r;
        }

        @Override
        public boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(o == null || getClass() != o.getClass())
                return false;

            Pair<?, ?> pair = (Pair<?, ?>) o;

            if(!Objects.equals(t, pair.t))
                return false;
            return Objects.equals(r, pair.r);
        }
    }
}