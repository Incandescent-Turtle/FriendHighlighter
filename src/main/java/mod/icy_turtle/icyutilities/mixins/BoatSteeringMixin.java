package mod.icy_turtle.icyutilities.mixins;

import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BoatEntity.class)
public class BoatSteeringMixin
{
//    @Redirect(method = "updatePaddles()V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setYaw(F)V"))
//    private void lockYaw(BoatEntity boat, float input) {
//        if(!IcyUtilitiesClient.isOrientationLocked)
//        {
//            boat.setYaw(input);
//        }
//    }

//    @Inject(method = "setInputs", at = @At("HEAD"), cancellable = true)
//    private void forceHighlight(CallbackInfo ci)
//    {
//        if(IcyUtilitiesClient.isOrientationLocked)
//        {
//            ci.cancel();
//        }
//    }

//    @Redirect(method = "setInputs", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/BoatEntity;pressingLeft:Z", opcode = Opcodes.GETFIELD))
//    private boolean injected(BoatEntity boat) {
//        return false;
//    }
}