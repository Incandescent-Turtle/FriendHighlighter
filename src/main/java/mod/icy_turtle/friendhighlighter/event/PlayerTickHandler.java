package mod.icy_turtle.friendhighlighter.event;//package mod.icy_turtle.friendhighlighter.event;

import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.Map;

/**
 *  For debug purposes, this will run code every tick.
 */
public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    static int i = 0;
    static MutableText txt = Text.literal("test");

    @Override
    public void onStartTick(MinecraftClient client)
    {
        i++;
        if(i >= 20)
        {
            i=0;
            var hitMap = FriendsListHandler.getRecentHitMap();
            Iterator<Map.Entry<LivingEntity, Integer>> iterator = hitMap.entrySet().iterator();

            // Iterate through the HashMap
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if(entry.getValue() <= 0)
                {
                    iterator.remove();
                } else {
                    hitMap.put(entry.getKey(), entry.getValue()-1);
                }
            }
        }
    }
}