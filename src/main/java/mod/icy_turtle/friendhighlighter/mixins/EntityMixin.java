package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public abstract void emitGameEvent(GameEvent event);

    @Shadow public abstract boolean entityDataRequiresOperator();

    @Shadow public abstract void emitGameEvent(GameEvent event, @Nullable Entity entity);

    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void forceHighlight(CallbackInfoReturnable<Boolean> cir)
    {
        if(FriendHighlighter.isHighlightEnabled)
        {
            Entity entity = (Entity)(Object)this;
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(friend != null && friend.outlineFriend && (entity instanceof PlayerEntity || !friend.onlyPlayers))
            {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void forceHighlightColor(CallbackInfoReturnable<Integer> cir)
    {
        if(FriendHighlighter.isHighlightEnabled)
        {
            Entity entity = (Entity)(Object)this;
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(friend != null && (entity instanceof PlayerEntity || !friend.onlyPlayers))
            {
                cir.setReturnValue(FHConfig.getInstance().getColorOrDefault(entity));
            }
        }
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void forceNameColor(CallbackInfoReturnable<Text> cir)
    {
        Entity entity = (Entity)(Object)this;

        if(FriendHighlighter.isHighlightEnabled)
        {
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(friend != null && (entity instanceof PlayerEntity || !friend.onlyPlayers))
            {
                cir.setReturnValue(FHUtils.getBoldAndColored(entity.getName().getString(), FHConfig.getInstance().getColorOrDefault(entity)));
            }
        }
    }
}