package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.Text;
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
        if(FriendHighlighter.isHighlightEnabled && (Entity)(Object)this instanceof PigEntity entity)
        {
            if(FHConfig.getInstance().mapContainsEntity(entity))
            {
                cir.setReturnValue(true);
            } else {
                System.out.println(entity.getName());
            }
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void forceHighlightColor(CallbackInfoReturnable<Integer> cir)
    {
        if(FriendHighlighter.isHighlightEnabled && (Entity)(Object)this instanceof PigEntity entity)
        {
            if(FHConfig.getInstance().mapContainsEntity(entity))
            {
                cir.setReturnValue(FHConfig.getInstance().getColorOrWhite(entity));
            }
        }
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void forceNameColor(CallbackInfoReturnable<Text> cir)
    {
        if(FriendHighlighter.isHighlightEnabled && (Entity)(Object)this instanceof PigEntity entity)
        {
            if(FHConfig.getInstance().mapContainsEntity(entity))
                cir.setReturnValue(FHUtils.colorText(entity.getName().getString(), FHConfig.getInstance().getColorOrWhite(entity)));
        }
    }

//    @Inject(method = "shouldRenderName", at = @At("HEAD"), cancellable = true)
//    private void shouldRenderName(CallbackInfoReturnable<Boolean> cir)
//    {
//        if(FriendHighlighter.isHighlightEnabled)
//            cir.setReturnValue(true);
//    }
//
//    @Inject(method = "hasCustomName", at = @At("HEAD"), cancellable = true)
//    private void hasCustomName(CallbackInfoReturnable<Boolean> cir)
//    {
//        if(FriendHighlighter.isHighlightEnabled)
//            cir.setReturnValue(true);
//    }
//
//
//    @Inject(method = "isCustomNameVisible", at = @At("HEAD"), cancellable = true)
//    private void isCustomNameVisible(CallbackInfoReturnable<Boolean> cir)
//    {
//        if(FriendHighlighter.isHighlightEnabled)
//            cir.setReturnValue(true);
//    }
}