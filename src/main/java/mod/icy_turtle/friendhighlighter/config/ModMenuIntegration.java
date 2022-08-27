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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.translatable("config.friendHighlighter.title"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory testing = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.test"));
            testing.addEntry(new NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>>(
                    Text.literal("Friend's List"),
                    mapToPlayerList(FHConfig.getInstance().friendsMap),
                    true,
                    Optional::empty,
                    list -> FHConfig.getInstance().friendsMap = playerListToMap(list),
                    () -> mapToPlayerList(FHConfig.getInstance().friendsMap),
                    entryBuilder.getResetButtonKey(),
                    true,
                    false,
                    (friend, nestedListListEntry) -> {
                        //  when adding a new player
                        if (friend == null) {
                            var defaultFriend = new HighlightedFriend();
                            return new MultiElementListEntry<>(
                                    Text.literal("Friend"), defaultFriend,
                                    List.of(entryBuilder.startTextField(Text.literal("Name"), defaultFriend.name)
                                                    .setSaveConsumer(str -> defaultFriend.name = str)
                                                    .build(),
                                            entryBuilder.startColorField(Text.literal("Color"), defaultFriend.color)
                                                    .setSaveConsumer(color -> defaultFriend.color = color)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Only Players"), defaultFriend.onlyPlayers)
                                                    .setSaveConsumer(color -> {})
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), defaultFriend.outlineFriend)
                                                    .setSaveConsumer(color -> {})
                                                    .build()),
                                    true);
                        //  when loading a player from the config
                        } else {
                            return new MultiElementListEntry<>(
                                    Text.literal(friend.name.equals("") ? "Friend" : friend.name), friend,
                                    List.of(entryBuilder.startTextField(Text.literal("Name"), friend.name)
                                                    .setSaveConsumer(str -> friend.name = str)
                                                    .build(),
                                            entryBuilder.startColorField(Text.literal("Color"), friend.color)
                                                    .setSaveConsumer(color -> friend.color = color)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Only Players"), friend.onlyPlayers)
                                                    .setSaveConsumer(onlyPlayers -> friend.onlyPlayers = onlyPlayers)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), friend.outlineFriend)
                                                    .setSaveConsumer(outlineFriend -> friend.outlineFriend = outlineFriend)
                                                    .build()),
                                    true);
                        }
                    }
            ));
            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
                FriendHighlighter.COMMAND_HANDLER.updateList(false,false);
            });
            return builder.setParentScreen(screen).build();
        };
    }

    private static Map<String, HighlightedFriend> playerListToMap(List<HighlightedFriend> list)
    {
        Map map = new HashMap();
        list.forEach(p -> map.put(p.name, p));
        return map;
    }

    private static List<HighlightedFriend> mapToPlayerList(Map<String, HighlightedFriend> map)
    {
        return map.values().stream().toList();
    }
}