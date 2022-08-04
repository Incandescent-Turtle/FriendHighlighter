package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void forceHighlight(CallbackInfoReturnable<Boolean> cir)
    {
//        if(((Entity)(Object)this) instanceof Entity)
//        {
            cir.setReturnValue(FriendHighlighter.isHighlightEnabled);
//        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void getTeamColor(CallbackInfoReturnable<Integer> cir)
    {
        int value = Color.WHITE.getRGB();

        if(((Entity)(Object)this) instanceof PigEntity)
        {
            if(FriendHighlighter.CONFIG() != null)
            {
                var list = FriendHighlighter.CONFIG().list;

                for (ModConfig.Player p : list)
                {
                    if(p.name.equals(((Entity)(Object)this).getDisplayName().getString()))
                    {
                        value = p.color;
                    }
                }
            }
        }

//        Entity entity = (Entity) (Object) this;
        cir.setReturnValue(value);
    }
}