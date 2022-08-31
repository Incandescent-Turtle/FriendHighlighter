package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.*;

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

            //  adding the friends list
            friendsList.addEntry(new NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>>(
                    Text.literal("Friend's List"),
                    mapToFriendsList(FHConfig.getInstance().friendsMap), // initial
                    true,
                    Optional::empty, //  tool tip
                    list -> FHConfig.getInstance().friendsMap = playerListToMap(list),
                    () -> mapToFriendsList(FHConfig.getInstance().friendsMap),
                    entryBuilder.getResetButtonKey(),
                    true,
                    true,
                    (friendIn, nestedListListEntry) -> {
                        final var friend = friendIn == null ? new HighlightedFriend() : friendIn;
                        return new MultiElementListEntry<>(
                                Text.literal(friend.name.equals("") ? "Friend" : friend.name), friend,
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
                                                .setTooltip(Text.literal("Whether only player's with this name will get highlighted"))
                                                .build(),
                                        entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), friend.outlineFriend)
                                                .setSaveConsumer(outlineFriend -> friend.outlineFriend = outlineFriend)
                                                .setTooltipSupplier(() -> Optional.of(new Text[]{Text.literal("Whether " + (friend.onlyPlayers ? "players" : "entities") + " with this name will be highlighted in addition their name tag always showing and being coloured")}))
                                                .build()),
                                friend.name.equals(""));
                    }
            ));
            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateList();
            });
            return builder.setParentScreen(screen).build();
        };
    }

    /**
     * Converts the given list of {@link HighlightedFriend}s to a map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
     * @param list the friends list to convert.
     * @return the map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
     */
    private static Map<String, HighlightedFriend> playerListToMap(List<HighlightedFriend> list)
    {
        Map<String, HighlightedFriend> map = new LinkedHashMap<>();
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
    private static List<HighlightedFriend> mapToFriendsList(Map<String, HighlightedFriend> map)
    {
        var list = new ArrayList<>(map.values());
        //  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
        Collections.reverse(list);
        return list;
    }
}