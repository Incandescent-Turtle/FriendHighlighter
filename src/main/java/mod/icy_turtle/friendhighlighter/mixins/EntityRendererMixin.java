package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
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
    //  to override whether the entities name tag should be rendered.
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    public boolean renderNameTag(EntityRenderer renderer, Entity entity) {
        if(FriendHighlighter.isHighlighterEnabled && FHSettings.getSettings().highlightWhenInvisible)
        {
            var friend = FriendsListHandler.getFriendFromEntity(entity);
            if(friend != null && friend.isEnabled() && (entity instanceof PlayerEntity || (!friend.onlyPlayers && entity.hasCustomName())))
                return true;
        }
        return renderer.hasLabel(entity);
    }

    //  to override the color the name tag should be rendered in, using its display name
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text forceNameColor(Entity entity)
    {
        if(FriendHighlighter.isHighlighterEnabled && FHSettings.getSettings().highlightWhenInvisible)
        {
            var friend = FriendsListHandler.getFriendFromEntity(entity);
            if(FriendsListHandler.shouldHighlightEntity(entity))
            {
                return FHUtils.getBoldAndColored(entity.getDisplayName().getString(), friend.color);
            }
        }
        return entity.getDisplayName();
    }
}
