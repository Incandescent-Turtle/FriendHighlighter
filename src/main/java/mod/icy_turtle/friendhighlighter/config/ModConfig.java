package mod.icy_turtle.friendhighlighter.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "friendhighlighter")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class ModConfig implements ConfigData
{
    public List<Player> list = new ArrayList<>();

    public static class Player
    {
        public String name = "";

        @ConfigEntry.ColorPicker
        public int color = 0xFFFFFF;
    }
}