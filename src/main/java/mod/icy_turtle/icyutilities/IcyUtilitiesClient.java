package mod.icy_turtle.icyutilities;

import mod.icy_turtle.icyutilities.event.KeyInputHandler;
import mod.icy_turtle.icyutilities.event.PlayerTickHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IcyUtilitiesClient implements ClientModInitializer {

    public static final String MOD_ID = "icyutilities";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float ANGLE_TO_LOCK_AT = 45;

    public static boolean isHighlightEnabled = false,
                            isOrientationLocked = false;

    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        ClientTickEvents.START_CLIENT_TICK.register(new PlayerTickHandler());
    }
}