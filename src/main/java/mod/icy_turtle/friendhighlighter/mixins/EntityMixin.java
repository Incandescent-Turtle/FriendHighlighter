package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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
                var list = FriendHighlighter.CONFIG().playerList;

                for (FHConfig.Player p : list)
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

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir)
    {
        if(((Entity)(Object)this) instanceof PigEntity entity)
        {
            if(FriendHighlighter.CONFIG() != null)
            {
                var list = FriendHighlighter.CONFIG().playerList;

                for (FHConfig.Player p : list)
                {
                    if(p.name.equals(entity.getName().getString()))
                    {
                        cir.setReturnValue(entity.getName().getWithStyle(Style.EMPTY.withColor(p.color)).get(0));
                    }
                }
            }
        }
    }

    @Inject(method = "shouldRenderName", at = @At("HEAD"), cancellable = true)
    private void shouldRenderName(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(true);
    }

    @Inject(method = "isCustomNameVisible", at = @At("HEAD"), cancellable = true)
    private void isCustomNameVisible(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(true);
    }
}