package mod.icy_turtle.friendhighlighter.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    @Override
    public void onStartTick(MinecraftClient client) {
        if(client.player == null) return;
    }
}