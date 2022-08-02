package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
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
        if(((Entity)(Object)this) instanceof PlayerEntity)
        {
            cir.setReturnValue(FriendHighlighter.isHighlightEnabled);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void getTeamColor(CallbackInfoReturnable<Integer> cir) {
//        Entity entity = (Entity) (Object) this;
        cir.setReturnValue(Color.RED.getRGB());
    }

    //isPartVisible in living enetitiy rendfeder
}