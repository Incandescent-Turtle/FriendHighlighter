package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    public boolean renderNameTag(EntityRenderer renderer, Entity entity) {
        if(FriendHighlighter.isHighlighterEnabled)
        {
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(friend != null && (entity instanceof PlayerEntity || (!friend.onlyPlayers && entity.hasCustomName())))
                return true;
        }
        return renderer.hasLabel(entity);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text forceNameColor(Entity entity)
    {
        if(FriendHighlighter.isHighlighterEnabled)
        {
            var friend = FHConfig.getInstance().getFriendFromEntity(entity);
            if(friend != null && (entity instanceof PlayerEntity || !friend.onlyPlayers))
            {
                return FHUtils.getBoldAndColored(entity.getDisplayName().getString(), FHConfig.getInstance().getColorOrDefault(entity));
            }
        }
        return entity.getDisplayName();
    }
}
