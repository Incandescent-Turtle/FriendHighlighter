package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

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

            ConfigCategory friendsList = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.friendsList"));
            friendsList.addEntry(createFriendsList(entryBuilder));

            ConfigCategory modSettings = builder.getOrCreateCategory(Text.literal("Mod Settings"));
            createModSettings(modSettings, entryBuilder);
            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateLists();
            });
            return builder.setParentScreen(screen).build();
        };
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
                                    entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), friend.outlineFriend)
                                            .setSaveConsumer(outlineFriend -> friend.outlineFriend = outlineFriend)
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
                entryBuilder.startEnumSelector(Text.literal("Message Display Method"), FHSettings.MessageDisplayMethod.class, FHSettings.getSettings().messageDisplayMethod)
                        .setSaveConsumer(displayMethod -> settings.messageDisplayMethod = displayMethod)
                        .setEnumNameProvider(displayMethod ->  Text.literal(FHUtils.capitalizeAllFirstLetters(displayMethod.name().replaceAll("_", " "))))
                        .setTooltipSupplier(createToolTip("How a message informing you of a change to your friends list is displayed. As a chat message, above the hotbar, or both."))
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
}