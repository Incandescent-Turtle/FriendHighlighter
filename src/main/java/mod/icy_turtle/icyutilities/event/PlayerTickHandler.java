package mod.icy_turtle.icyutilities.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class PlayerTickHandler implements ClientTickEvents.StartTick {

    @Override
    public void onStartTick(MinecraftClient client) {
//        if(client.player == null || client.player.getVehicle() == null || !(client.player.getVehicle() instanceof BoatEntity)) return;
//        var boat = client.player.getVehicle();
    }
}
