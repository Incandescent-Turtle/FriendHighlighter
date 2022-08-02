package mod.icy_turtle.friendhighlighter.event;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.util.ModUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyInputHandler
{
    public static final String KEY_CATEGORY_ICY_UTILITIES = "key.category.friendhighlighter.utils";

    public static final String
            KEY_HIGHLIGHT_PLAYERS = "key.friendhighlighter.highlight";

    public static KeyBinding highlightKey;

    public static void register()
    {
        highlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_HIGHLIGHT_PLAYERS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::registerKeyInputs);
    }

    private static void registerKeyInputs(MinecraftClient client)
    {
        if (client.player == null) return;
        var player = client.player;

        if (highlightKey.wasPressed())
        {
            FriendHighlighter.isHighlightEnabled = !FriendHighlighter.isHighlightEnabled;

            Text state = FriendHighlighter.isHighlightEnabled
                    ? getPositiveMessage("ENABLED")
                    : getNegativeMessage("DISABLED");
            player.sendMessage(Texts.join(List.of(Text.of("SO highlight"), state), Text.of(" ")));
        }
    }

    private static Text getPositiveMessage(String msg)
    {
        return ModUtils.getBoldAndColored(msg, Formatting.GREEN).get(0);
    }

    private static Text getNegativeMessage(String msg)
    {
        return ModUtils.getBoldAndColored(msg, Formatting.RED).get(0);
    }
}