package mod.icy_turtle.friendhighlighter.event;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.ModMenuIntegration;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 *  Is used to register and define the functionality of custom keybindings.
 */
public class KeyInputHandler
{
    /**
     * The language key for the key category for FriendHighlighter's keybindings.
     */
    private static final String KEY_CATEGORY_ICY_UTILITIES = "key.category.friendhighlighter.utils";

    /**
     * The language key for the key to toggle the highlighting feature.
     */
    private static final String KEY_HIGHLIGHT_FRIENDS = "key.friendhighlighter.highlight";
    private static final String KEY_OPEN_GUI = "key.friendhighlighter.opengui";
    private static final String KEY_HIGHLIGHT_ALL_PLAYERS = "key.friendhighlighter.highlightplayers";

    /**
     * The {@link KeyBinding} to toggle the highlighting feature.
     */
    private static KeyBinding highlightKey, openGUIKey, highlightPlayers;

    /**
     * Registers the mod's {@link KeyBinding}s.
     */
    public static void register()
    {
        highlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(KEY_HIGHLIGHT_FRIENDS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        openGUIKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(KEY_OPEN_GUI,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        highlightPlayers = KeyBindingHelper.registerKeyBinding(new KeyBinding(KEY_HIGHLIGHT_ALL_PLAYERS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                KEY_CATEGORY_ICY_UTILITIES
        ));
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::registerKeyInputs);
    }

    private static void registerKeyInputs(MinecraftClient client)
    {
        if (highlightKey.wasPressed())
        {
            //  hacky solution, makes sure to send chat notification is selected
            FriendHighlighter.enterHitAt = System.currentTimeMillis();
            FriendHighlighter.toggleHighlight();
        }
        if(openGUIKey.wasPressed()) {
            if(FabricLoader.getInstance().isModLoaded("modmenu")) {
                MinecraftClient.getInstance().setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(null));
            } else {
                FriendHighlighter.sendMessage(FHUtils.getNegativeMessage("To use the GUI download Mod Menu and Cloth Config"));
            }
        }
        if(highlightPlayers.wasPressed()) {
            FHSettings.getSettings().highlightAllPlayers = !FHSettings.getSettings().highlightAllPlayers;
            FriendHighlighter.sendMessage(Text.of("All player highlight toggled"));
            FriendHighlighter.COMMAND_HANDLER.updateLists();
        }
    }
}