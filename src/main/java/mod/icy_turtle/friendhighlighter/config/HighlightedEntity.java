package mod.icy_turtle.friendhighlighter.config;

import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;

public class HighlightedEntity extends HighlightedBase
{
    public HighlightedEntity(EntityType type)
    {
        super(FHSettings.getSettings().defaultColor, FHUtils.getNameFromEntityType(type).getString());
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean isJustNameTag()
    {
        return false;
    }

    @Override
    public boolean isOnlyPlayers()
    {
        return false;
    }
}