package mod.icy_turtle.friendhighlighter.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 *  For debug purposes, this will run code every tick.
 */
public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    static int i =0;
    static MutableText txt = Text.literal("test");
    @Override
    public void onStartTick(MinecraftClient client)
    {

    }
}