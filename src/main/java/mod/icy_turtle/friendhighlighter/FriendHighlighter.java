package mod.icy_turtle.friendhighlighter;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.icy_turtle.friendhighlighter.commands.Commands;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.event.KeyInputHandler;
import mod.icy_turtle.friendhighlighter.event.PlayerTickHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/*
    TODO:

 */
public class FriendHighlighter implements ClientModInitializer
{
    public static final String MOD_ID = "friendhighlighter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static FHConfig CONFIG;

    public static boolean isHighlightEnabled = false;

    @Override
    public void onInitializeClient()
    {
        KeyInputHandler.register();
        ClientTickEvents.START_CLIENT_TICK.register(new PlayerTickHandler());
        AutoConfig.register(FHConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(FHConfig.class).getConfig();
        ClientCommandRegistrationCallback.EVENT.register(Commands::registerCommands);
    }

    public static int toggleHighlight()
    {
        FriendHighlighter.isHighlightEnabled = !FriendHighlighter.isHighlightEnabled;

        Text state = FriendHighlighter.isHighlightEnabled
                ? FHUtils.getPositiveMessage("ENABLED")
                : FHUtils.getNegativeMessage("DISABLED");
        MinecraftClient.getInstance().player.sendMessage(Texts.join(List.of(Text.of("Friend highlight"), state), Text.of(" ")), true);
        return 1;
    }

    public static final FHConfig CONFIG()
    {
        return CONFIG;
    }

}