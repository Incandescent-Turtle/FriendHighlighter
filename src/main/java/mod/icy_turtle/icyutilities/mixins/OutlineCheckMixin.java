package mod.icy_turtle.icyutilities.mixins;

import mod.icy_turtle.icyutilities.IcyUtilitiesClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(Entity.class)
public class OutlineCheckMixin {

    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir)
    {
        if(((Entity)(Object)this) instanceof PlayerEntity)
        {
            cir.setReturnValue(IcyUtilitiesClient.isHighlightEnabled);
        } else if(((Entity)(Object)this) instanceof CowEntity animal)
        {
            try {
                var str = "ticks: " + animal.getLoveTicks() + " | ";
                Field cooldown = AnimalEntity.class.getDeclaredField("BREEDING_COOLDOWN");
                cooldown.setAccessible(true);
                str+="cooldown" + cooldown.get(animal) + " | ";
                System.out.println(animal.canEat());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
