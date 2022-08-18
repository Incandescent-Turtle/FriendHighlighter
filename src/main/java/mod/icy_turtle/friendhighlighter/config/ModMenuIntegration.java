package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    class Pair<T, R>
    {
        T t;
        R r;

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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair<?, ?> pair = (Pair<?, ?>) o;

            if (!Objects.equals(t, pair.t)) return false;
            return Objects.equals(r, pair.r);
        }
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.translatable("config.friendHighlighter.title"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory testing = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.test"));
            testing.addEntry(new NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>>(
                    Text.literal("Friend's List"),
                    mapToPlayerList(FHConfig.getInstance().playerMap),
                    true,
                    Optional::empty,
                    list -> FHConfig.getInstance().playerMap = playerListToMap(list),
                    () -> mapToPlayerList(FHConfig.getInstance().playerMap),
                    entryBuilder.getResetButtonKey(),
                    true,
                    false,
                    (player, nestedListListEntry) -> {
                        //  when adding a new player
                        if (player == null) {
                            var defaultPlayer = new HighlightedFriend();
                            return new MultiElementListEntry<>(
                                    Text.literal("Player"), defaultPlayer,
                                    List.of(entryBuilder.startTextField(Text.literal("Name"), defaultPlayer.name)
                                                    .setSaveConsumer(str -> defaultPlayer.name = str)
                                                    .build(),
                                            entryBuilder.startColorField(Text.literal("Color"), defaultPlayer.color)
                                                    .setSaveConsumer(color -> defaultPlayer.color = color)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Only Players"), defaultPlayer.onlyPlayers)
                                                    .setSaveConsumer(color -> {})
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), defaultPlayer.outlineFriend)
                                                    .setSaveConsumer(color -> {})
                                                    .build()),
                                    true);
                        //  when loading a player from the config
                        } else {
                            return new MultiElementListEntry<>(
                                    Text.literal(player.name.equals("") ? "Player" : player.name), player,
                                    List.of(entryBuilder.startTextField(Text.literal("Name"), player.name)
                                                    .setSaveConsumer(str -> player.name = str)
                                                    .build(),
                                            entryBuilder.startColorField(Text.literal("Color"), player.color)
                                                    .setSaveConsumer(color -> player.color = color)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Only Players"), player.onlyPlayers)
                                                    .setSaveConsumer(onlyPlayers -> player.onlyPlayers = onlyPlayers)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.literal("Outline Friend"), player.outlineFriend)
                                                    .setSaveConsumer(outlineFriend -> player.outlineFriend = outlineFriend)
                                                    .build()),
                                    true);
                        }
                    }
            ));
            builder.setSavingRunnable(()->{
                FHConfig.saveConfig();
                FHConfig.loadConfig();
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