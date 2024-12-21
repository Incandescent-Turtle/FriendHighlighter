package mod.icy_turtle.friendhighlighter.mixins;//package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    // stores a variable for use in the enhanced nametag mixin
    private Entity currentEntity;
    @Inject(method = "renderLabelIfPresent", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void captureEntity(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
        this.currentEntity = entity;
    }

    //  to override whether the entities name tag should be rendered (ei. when far away).
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    public boolean renderNameTag(EntityRenderer renderer, Entity entity) {
        if(FriendsListHandler.shouldRenderNametag(entity))
        {
            return true;
        }
        return renderer.hasLabel(entity);
    }

    //  to override the color the name tag should be rendered in, using its display name
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    private Text forceNameColor(Entity entity)
    {
        var friend = FriendsListHandler.getFriendFromEntity(entity);
        if(FriendsListHandler.shouldHighlightEntity(entity))
        {
            return FHUtils.getBoldAndColored(entity.getDisplayName().getString(), friend.getColor());
        }
        return entity.getDisplayName();
    }

    // forces nametag overlay to be transparent and forces nametag visibilty
    @ModifyArgs(method = "renderLabelIfPresent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    private void modifyNametagRendering(Args args) {
        if(currentEntity == null)
        {
            return;
        }
        if (FriendsListHandler.shouldHighlightEntity(currentEntity))
        {
            if(FHSettings.getSettings().enhancedNametags)
            {
                args.set(3, 0xFFFFFFFF);
                // enlarging nametag text
                //            args.set(5, ((Matrix4f) args.get(5)).scale(5,5,5));
            }
        }
    }

    // renders nametag while sneaking
    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaky()Z"))
    private boolean redirectIsSneaky(Entity entity) {
        if(FriendsListHandler.shouldHighlightEntity(entity))
        {
            return false;
        }
        return entity.isSneaky();
    }
}