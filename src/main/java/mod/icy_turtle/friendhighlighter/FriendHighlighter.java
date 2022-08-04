package mod.icy_turtle.friendhighlighter;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.icy_turtle.friendhighlighter.config.ModConfig;
import mod.icy_turtle.friendhighlighter.event.KeyInputHandler;
import mod.icy_turtle.friendhighlighter.event.PlayerTickHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendHighlighter implements ClientModInitializer
{
    public static final String MOD_ID = "friendhighlighter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ModConfig CONFIG;

    public static boolean isHighlightEnabled = false;

    @Override
    public void onInitializeClient()
    {
        KeyInputHandler.register();
        ClientTickEvents.START_CLIENT_TICK.register(new PlayerTickHandler());
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static final ModConfig CONFIG()
    {
        return CONFIG;
    }
}