package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method="tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci)
    {
    }

    @Inject(method="damage", at = @At(value = "HEAD"))
    private void dam(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        assert MinecraftClient.getInstance().world != null;
        if (source == null)
        {
            return;
        }
        var livingEntity = (LivingEntity) (Object) this;
        // either attacker is the player, or a projectile the player has launched
        if (Objects.equals(source.getAttacker(), MinecraftClient.getInstance().player) || (source.getSource() instanceof ProjectileEntity pe && Objects.equals(pe.getOwner(), MinecraftClient.getInstance().player)))
        {
            System.out.println("placing");
            FriendsListHandler.getRecentHitMap().put(livingEntity, FHSettings.getSettings().hitHighlightSeconds);
        }
    }
}