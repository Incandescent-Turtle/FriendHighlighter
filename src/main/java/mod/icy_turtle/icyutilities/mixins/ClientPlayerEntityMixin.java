package mod.icy_turtle.icyutilities.mixins;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
    @Redirect(method = "tickRiding",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setInputs(ZZZZ)V"))
    private void lockYaw(BoatEntity boat, boolean left, boolean right, boolean forward, boolean back) {
        if(IcyUtilitiesClient.isOrientationLocked)
        {
            boat.setInputs(false, false, forward, back);
        } else {
            boat.setInputs(left, right,forward,back);
        }
    }
}