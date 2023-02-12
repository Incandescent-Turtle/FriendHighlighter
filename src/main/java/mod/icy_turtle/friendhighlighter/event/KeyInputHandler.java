package mod.icy_turtle.friendhighlighter.event;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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

    /**
     * The {@link KeyBinding} to toggle the highlighting feature.
     */
    private static KeyBinding highlightKey;

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
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::registerKeyInputs);
    }

    private static void registerKeyInputs(MinecraftClient client)
    {
        if (highlightKey.wasPressed())
        {
            //  hacky solution, makes sure to send chat notification is selected
            FriendHighlighter.enterHitAt = System.currentTimeMillis();
            FriendHighlighter.toggleHighlight();
//            MinecraftClient.getInstance().setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(null));

        }
    }
}