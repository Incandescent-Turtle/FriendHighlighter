package mod.icy_turtle.icyutilities.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class PlayerTickHandler implements ClientTickEvents.StartTick
{
    @Override
    public void onStartTick(MinecraftClient client) {
        if(client.player == null) return;
        try
        {
            var field = KeyBinding.class.getDeclaredField("KEYS_BY_ID");
            field.setAccessible(true);
            System.out.println(field.get(null));
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}