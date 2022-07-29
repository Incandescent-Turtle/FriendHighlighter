package mod.icy_turtle.icyutilities.event;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import mod.icy_turtle.icyutilities.util.ModUtils;
import mod.icy_turtle.icyutilities.util.RotationUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyInputHandler
{
    public static final String KEY_CATEGORY_ICY_UTILITIES = "key.category.icyutilities.utils";

    public static final String KEY_ALIGN_PLAYER = "key.icyutilities.align",
            KEY_HIGHLIGHT_PLAYERS = "key.icyutilities.highlight",
            KEY_ORIENTATION_LOCK = "key.icyutilities.lockOrientation";

    public static KeyBinding alignKey, highlightKey, orientationLockKey;

    public static void register()
    {
        alignKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ALIGN_PLAYER,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        highlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_HIGHLIGHT_PLAYERS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        orientationLockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ORIENTATION_LOCK,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                KEY_CATEGORY_ICY_UTILITIES
        ));

        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::registerKeyInputs);
    }

    private static void registerKeyInputs(MinecraftClient client)
    {
        if (client.player == null) return;
        var player = client.player;

        if (alignKey.wasPressed())
        {
            if (player.getVehicle() instanceof BoatEntity boat)
            {
                RotationUtils.snapBoat(boat);
            }
        }

        if (highlightKey.wasPressed())
        {
            IcyUtilitiesClient.isHighlightEnabled = !IcyUtilitiesClient.isHighlightEnabled;

            Text state = IcyUtilitiesClient.isHighlightEnabled
                    ? getPositiveMessage("ENABLED")
                    : getNegativeMessage("DISABLED");
            player.sendMessage(Texts.join(List.of(Text.of("SO highlight"), state), Text.of(" ")));
        }

        if (orientationLockKey.wasPressed())
        {
            IcyUtilitiesClient.isOrientationLocked = !IcyUtilitiesClient.isOrientationLocked;
            Text state = !IcyUtilitiesClient.isOrientationLocked
                    ? getPositiveMessage("UNLOCKED")
                    : getNegativeMessage("LOCKED");
            player.sendMessage(Texts.join(List.of(Text.of("Orientation is"), state), Text.of(" ")));
            if (player.getVehicle() instanceof BoatEntity boat)
            {
                RotationUtils.snapBoat(boat);
            }
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