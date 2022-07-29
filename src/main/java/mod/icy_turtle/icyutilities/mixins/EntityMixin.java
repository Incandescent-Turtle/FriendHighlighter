package mod.icy_turtle.icyutilities.mixins;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void forceHighlight(CallbackInfoReturnable<Boolean> cir)
    {
        if(((Entity)(Object)this) instanceof PlayerEntity)
        {
            cir.setReturnValue(IcyUtilitiesClient.isHighlightEnabled);
        }
    }

//    @Redirect(method = "changeLookDirection(DD)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setYaw(F)V"))
//    private void lockYaw(Entity boat, float input) {
//        if(!IcyUtilitiesClient.isOrientationLocked)
//        {
//            boat.setYaw(input);
//        }
//    }
}