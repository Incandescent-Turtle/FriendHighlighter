package mod.icy_turtle.friendhighlighter.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 *  For debug purposes, this will run code every tick.
 */
public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    static int i =0;

    @Override
    public void onStartTick(MinecraftClient client)
    {
//        System.out.println(Arrays.stream(FHSettings.MessageDisplayMethod.values()).map(dm -> dm.name().toLowerCase()).collect(
//                Collectors.toList()));
    }
}