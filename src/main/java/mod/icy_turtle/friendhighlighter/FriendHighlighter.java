package mod.icy_turtle.friendhighlighter;

import com.mojang.brigadier.Command;
import mod.icy_turtle.friendhighlighter.commands.CommandHandler;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.event.KeyInputHandler;
import mod.icy_turtle.friendhighlighter.event.PlayerTickHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
    TODO:
        chat message wont be sent when sleeping, need to add hook for that.
        add option to disable tooltips
        make tooltips smaller
        make it so mod integration and cloth config arent necessary

        broken:
        add drop down menu in config menu to add friends
        allow using color names in the GUI
            - custom dropdown
            - custom parsing
            - custom storage (as string)
            - color widget
            - color picker?
*/

/**
 * The central class for the mod, that allows access to mod-wide values and objects.
 */
public class FriendHighlighter implements ClientModInitializer
{
    public static final String MOD_ID = "friendhighlighter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommandHandler COMMAND_HANDLER = new CommandHandler();

    /**
     *  Whether friends should be highlighted/have their names be colored.
     */
    public static boolean isHighlighterEnabled = false;

    /**
     * The time stamp of when a chat message was sent with enter (closing the chat window, not while sleeping).
     */
    public static long enterHitAt = 0;

    public static boolean logChatMessages = true;

    @Override
    public void onInitializeClient()
    {
        KeyInputHandler.register();
        ClientTickEvents.START_CLIENT_TICK.register(new PlayerTickHandler());
        ClientCommandRegistrationCallback.EVENT.register(COMMAND_HANDLER::registerCommands);
    }

    /**
     * Sends a message via either chat or the action bar, depending on {@link FHSettings#messageDisplayMethod}.
     * @param message the message to send
     */
    public static void sendMessage(Text message)
    {
        var player = MinecraftClient.getInstance().player;
        var screen = MinecraftClient.getInstance().currentScreen;
        /*
                    TODO:
                            wont send chat messages when sleeping at all
                            needs enter hook added there too
         */
        final boolean shouldSendChatMessage = !(screen instanceof SleepingChatScreen)
                && (!(screen instanceof ChatScreen) || (System.currentTimeMillis()-enterHitAt)<1000);

        switch(FHSettings.getSettings().messageDisplayMethod)
        {
            case ACTION_BAR -> player.sendMessage(message, true);
            case CHAT -> {
                if(shouldSendChatMessage)
                    player.sendMessage(message, false);
            }

            case BOTH -> {
                player.sendMessage(message, true);
                if(shouldSendChatMessage)
                    player.sendMessage(message, false);
            }
        }
    }

    /**
     * Turns the highlighter on or off. displays the new highlighter state on the players action bar.
     * @return {@link Command#SINGLE_SUCCESS} to signify a success when used in commands.
     * @see FriendHighlighter#isHighlighterEnabled
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
        FriendHighlighter.sendMessage(text);
        COMMAND_HANDLER.updateList();
        return Command.SINGLE_SUCCESS;
    }
}