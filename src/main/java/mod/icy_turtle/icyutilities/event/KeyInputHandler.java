package mod.icy_turtle.icyutilities.event;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import mod.icy_turtle.icyutilities.IcyUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.List;

public class    KeyInputHandler {
    public static final String KEY_CATEGORY_ICY_UTILITIES = "key.category.icyutilities.utils";

    public static final String KEY_ALIGN_PLAYER = "key.icyutilities.align",
                                KEY_HIGHLIGHT_PLAYERS = "key.icyutilities.highlight",
                                KEY_ORIENTATION_LOCK = "key.icyutilities.lock_orientation";

    public static KeyBinding alignKey, highlightKey, orientationLockKey;

    public static void register() {
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

        registerKeyInputs();
    }

    private static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(alignKey.wasPressed()) {
                if(client.player == null) return;
                IcyUtils.lockToClosestLockAngle(client.player);

                if(client.player.getVehicle() instanceof BoatEntity boat)
                {
                    IcyUtils.lockToClosestLockAngle(boat);
                    try {
                        Field yawVel = BoatEntity.class.getDeclaredField("yawVelocity");
                        yawVel.setAccessible(true);
                        yawVel.set(boat, 0);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            } else if(highlightKey.wasPressed()) {
                IcyUtilitiesClient.isHighlightEnabled = !IcyUtilitiesClient.isHighlightEnabled;
                var player = client.player;
                List<Text> state = null;
                if(IcyUtilitiesClient.isHighlightEnabled) {
                    state = Text.of("ENABLED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN));
                } else {
                    state = Text.of("DISABLED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED));
                }
                state.add(0, Text.of("SO highlight"));
                player.sendMessage(Texts.join(state, Text.of(" ")));
            } else if(orientationLockKey.wasPressed()) {
                boolean isUnlocking = IcyUtilitiesClient.isOrientationLocked;
                var player = client.player;
                List<Text> state = null;
                if(isUnlocking) {
                    state = Text.of("UNLOCKED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN));
                } else {
                    state = Text.of("LOCKED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED));
                }
                state.add(0, Text.of("Orientation is"));
                player.sendMessage(Texts.join(state, Text.of(" ")));

                if(isUnlocking)
                {
                    IcyUtils.lockToClosestLockAngle(player);
                    if(player.getVehicle() instanceof BoatEntity boat)
                    {
                        IcyUtils.lockToClosestLockAngle(boat);
                        player.setYaw(IcyUtils.roundToClosestLockAngle(boat.getYaw()));
                    }
                }

                IcyUtilitiesClient.isOrientationLocked = !IcyUtilitiesClient.isOrientationLocked;
            }
        });
    }
}
