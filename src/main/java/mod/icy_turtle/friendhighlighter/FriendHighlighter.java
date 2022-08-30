package mod.icy_turtle.friendhighlighter;

import com.mojang.brigadier.Command;
import mod.icy_turtle.friendhighlighter.commands.CommandHandler;
import mod.icy_turtle.friendhighlighter.event.KeyInputHandler;
import mod.icy_turtle.friendhighlighter.event.PlayerTickHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
    TODO:
    make it so mod integration and cloth config arent necessary

    split cmds into separate classes, see vanilla
    enable/disable friends - /fh enable and /fh disable

    borked
    add drop down menu in config menu to add friends
*/
public class FriendHighlighter implements ClientModInitializer
{
    public static final String MOD_ID = "friendhighlighter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommandHandler COMMAND_HANDLER = new CommandHandler();

    /**
     *  whether friends should be highlighted/have their names be colored
     */
    public static boolean isHighlighterEnabled = false;


    @Override
    public void onInitializeClient()
    {
        KeyInputHandler.register();
        ClientTickEvents.START_CLIENT_TICK.register(new PlayerTickHandler());
        ClientCommandRegistrationCallback.EVENT.register(COMMAND_HANDLER::registerCommands);
    }

    /**
     * turns the highlighter on or off. displays the new highlighter state on the players action bar
     * @return {@link Command#SINGLE_SUCCESS} to signify a success when used in commands
     */
    public static int toggleHighlight()
    {
        FriendHighlighter.isHighlighterEnabled = !FriendHighlighter.isHighlighterEnabled;

        //  sending activation status on action bar
        MutableText text = Text.literal("");
        text.append("Friend Highlighter")
                .append(" ")
                .append(
                        FriendHighlighter.isHighlighterEnabled
                        ? FHUtils.getPositiveMessage("ENABLED")
                        : FHUtils.getNegativeMessage("DISABLED")
        );
        MinecraftClient.getInstance().player.sendMessage(text, true);
        return Command.SINGLE_SUCCESS;
    }
}