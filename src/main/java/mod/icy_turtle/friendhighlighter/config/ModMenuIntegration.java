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
import java.util.stream.Collectors;

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
            testing.addEntry(new NestedListListEntry<HighlightedPlayer, MultiElementListEntry<HighlightedPlayer>>(
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
                            var defaultPlayer = new HighlightedPlayer();
                            return new MultiElementListEntry<>(
                                    Text.literal("Player"), defaultPlayer,
                                    List.of(entryBuilder.startTextField(Text.literal("Name"), defaultPlayer.name)
                                                    .setSaveConsumer(str -> defaultPlayer.name = str)
                                                    .build(),
                                            entryBuilder.startColorField(Text.literal("Color"), defaultPlayer.color)
                                                    .setSaveConsumer(color -> defaultPlayer.color = color)
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

    private static Map<String, Integer> playerListToMap(List<HighlightedPlayer> list)
    {
        Map map = new HashMap();
        list.forEach(p -> map.put(p.name, p.color));
        return map;
    }

    private static List<HighlightedPlayer> mapToPlayerList(Map<String, Integer> map)
    {
        return map.keySet().stream().map(name -> new HighlightedPlayer(name, map.get(name))).collect(Collectors.toList());
    }
}