package mod.icy_turtle.icyutilities.mixins;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import mod.icy_turtle.icyutilities.IcyUtils;
import net.fabricmc.fabric.mixin.networking.accessor.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

//    @Inject(method = "setYaw()V", at = @At("HEAD"), cancellable = true)
//    private void lockRotation(CallbackInfo ci)
//    {
//        if(IcyUtilitiesClient.isOrientationLocked && (((Entity)(Object)this) instanceof PlayerEntity || ((Entity)(Object)this) instanceof BoatEntity))
//        {
//            ci.cancel();
////            cir.setReturnValue(0F);
////            float yaw = ((YawAccessor) this).getYaw();
////            cir.setReturnValue(IcyUtils.roundToClosestLockAngle(yaw));
//        }
//    }

    @Redirect(method = "changeLookDirection(DD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setYaw(F)V"))
    private void setYaw(Entity entity, float input) {
        if(!IcyUtilitiesClient.isOrientationLocked)
        {
            entity.setYaw(input);
        }
    }
}