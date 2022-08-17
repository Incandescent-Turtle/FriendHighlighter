package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    public boolean renderNameTag(EntityRenderer er, Entity e) {
        if(FriendHighlighter.isHighlightEnabled && FHConfig.getInstance().mapContainsEntity(e))
            return true;
        try
        {
            Method method = EntityRenderer.class.getDeclaredMethod("hasLabel", Entity.class);
            method.setAccessible(true);
            return (boolean) method.invoke(er, e);
        } catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        } catch (InvocationTargetException ex)
        {
            ex.printStackTrace();
        } catch (IllegalAccessException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
