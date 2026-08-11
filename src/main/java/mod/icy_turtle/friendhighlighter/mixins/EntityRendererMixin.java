package mod.icy_turtle.friendhighlighter.mixins;

import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
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
public abstract class EntityRendererMixin<T extends Entity>
{
    @Shadow
    protected abstract boolean hasLabel(T entity, double squaredDistance);

    // stores the entity being processed during updateRenderState, for use by the redirects below
    private Entity currentEntity;

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void captureEntity(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci)
    {
        this.currentEntity = entity;
    }

    // whether the entity's name tag should be rendered (e.g. when far away)
    @SuppressWarnings("unchecked")
    @Redirect(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;D)Z"))
    public boolean renderNameTag(EntityRenderer<T, ?> renderer, Entity entity, double distance)
    {
        if (FriendsListHandler.shouldRenderNametag(entity))
        {
            return true;
        }
        return this.hasLabel((T) entity, distance);
    }

    // the color the name tag should be rendered in, using its display name
    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
	private Text forceNameColor(Entity entity)
    {
        var friend = FriendsListHandler.getFriendFromEntity(entity);
        if (FriendsListHandler.shouldHighlightEntity(entity))
        {
            return FHUtils.getBoldAndColored(entity.getDisplayName().getString(), friend.getColor());
        }
        return entity.getDisplayName();
    }

    // forces nametag background to be transparent / forces visibility
    @ModifyArgs(method = "renderLabelIfPresent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"))
    private void modifyNametagRendering(Args args)
    {
        if (currentEntity == null)
        {
            return;
        }
        if (FriendsListHandler.shouldHighlightEntity(currentEntity))
        {
            if (FHSettings.getSettings().enhancedNametags)
            {
                args.set(2, 0xFFFFFFFF); // background color arg
            }
        }
    }

    // renders nametag while sneaking
    @Redirect(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaky()Z"))
    private boolean redirectIsSneaky(Entity entity)
    {
        if (FriendsListHandler.shouldHighlightEntity(entity))
        {
            return false;
        }
        return entity.isSneaky();
    }
}