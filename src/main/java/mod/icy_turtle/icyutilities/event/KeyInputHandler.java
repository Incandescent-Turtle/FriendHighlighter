package mod.icy_turtle.icyutilities.event;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
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

public class KeyInputHandler {
    public static final String KEY_CATEGORY_ICY_UTILITIES = "key.category.icyutilities.utils";

    public static final String KEY_ALIGN_PLAYER = "key.icyutilities.align",
                                KEY_HIGHLIGHT_PLAYERS = "key.icyutilities.highlight";

    public static KeyBinding alignKey, highlightKey;

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

        registerKeyInputs();
    }

    private static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(alignKey.wasPressed()) {
                if(client.player == null) return;
                var playerYaw = client.player.getYaw();
                client.player.setYaw(Math.round(playerYaw/45)*45);

                if(client.player.getVehicle() == null || !(client.player.getVehicle() instanceof BoatEntity)) return;
                var boat = client.player.getVehicle();

                var boatYaw = boat.getYaw();
                boat.setYaw(Math.round(boatYaw/45)*45);

                try {
                    Field yawVel = BoatEntity.class.getDeclaredField("yawVelocity");
                    yawVel.setAccessible(true);
                    yawVel.set(boat, 0);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else if(highlightKey.wasPressed()) {
                IcyUtilitiesClient.isHighlightEnabled = !IcyUtilitiesClient.isHighlightEnabled;
                var player = client.player;
                var text = Text.of("SO highlighter ");
                List<Text> state = null;
                if(IcyUtilitiesClient.isHighlightEnabled) {
                    state = Text.of("ENABLED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN));
                } else {
                    state = Text.of("DISABLED").getWithStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED));
                }
                state.add(0, Text.of("SO highlight"));
                player.sendMessage(Texts.join(state, Text.of(" ")));
            }
        });
    }
}
