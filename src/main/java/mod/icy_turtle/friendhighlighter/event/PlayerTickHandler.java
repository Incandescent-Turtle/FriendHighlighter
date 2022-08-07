package mod.icy_turtle.friendhighlighter.event;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    @Override
    public void onStartTick(MinecraftClient client)
    {
        if(client.player == null) return;
        if(FriendHighlighter.CONFIG() == null) return;
//        System.out.println(FriendHighlighter.CONFIG().list);
    }
}