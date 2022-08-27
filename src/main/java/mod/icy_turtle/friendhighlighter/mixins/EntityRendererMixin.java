package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    public boolean renderNameTag(EntityRenderer er, Entity e) {
        if(FriendHighlighter.isHighlightEnabled)
        {
            var friend = FHConfig.getInstance().getFriendFromEntity(e);
            if(friend != null && (e instanceof PlayerEntity || (!friend.onlyPlayers && e.hasCustomName())))
                return true;
        }
        try
        {
            Method method = EntityRenderer.class.getDeclaredMethod("hasLabel", Entity.class);
            method.setAccessible(true);
            return (boolean) method.invoke(er, e);
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
