package mod.icy_turtle.friendhighlighter.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "friendhighlighter")
public class FHConfig implements ConfigData
{
    public List<Player> playerList = new ArrayList<>();

    public static class Player
    {
        public Player(){}

        public Player(String name, int color)
        {
            this.name = name;
            this.color = color;
        }

        public String name = "";

        @ConfigEntry.ColorPicker
        public int color = 0xFFFFFF;
    }
}