package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    //  to override whether the entity should have a glowing outline.
    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void forceHighlight(CallbackInfoReturnable<Boolean> cir)
    {
        if(FriendHighlighter.isHighlighterEnabled)
        {
            Entity entity = (Entity)(Object)this;
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(FHConfig.getInstance().shouldHighlightEntity(entity) && friend.outlineFriend)
            {
                cir.setReturnValue(true);
            }
        }
    }
}