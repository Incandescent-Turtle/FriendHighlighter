package mod.icy_turtle.icyutilities.event;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import mod.icy_turtle.icyutilities.IcyUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.BoatEntity;

public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    @Override
    public void onStartTick(MinecraftClient client) {
        if(client.player == null) return;
    }
}