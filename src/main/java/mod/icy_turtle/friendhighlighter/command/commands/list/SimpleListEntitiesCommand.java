package mod.icy_turtle.friendhighlighter.command.commands.list;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedEntity;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SimpleListEntitiesCommand extends Command
{
    public SimpleListEntitiesCommand(CommandHandler cmdHandler)
    {
        super(cmdHandler);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
    {
        return literal("listEntities")
                .executes(context -> cmdHandler.simpleEntityListChatMsg.sendInChat());
    }

    public static MutableText createSimpleEntityList()
    {
        MutableText txt = Text.literal("").append(CommandUtils.addHoverAndClickEvent(
                FHUtils.getMessageWithConnotation("Entity List: ", FriendHighlighter.isHighlighterEnabled),
                "Click to toggle Friend Highlighter",
                "/fh toggle"));
        var map = FriendsListHandler.getEntityMap().entrySet().stream().filter(e -> e.getValue().isEnabled()).collect(Collectors.toSet());
        if(map.isEmpty())
        {
            return txt.append(" Empty.");
        }
        var itr = map.iterator();
        while(itr.hasNext())
        {
            var friend = itr.next().getValue();
            txt.append(createToggleableEntityName(friend))
                    .append(itr.hasNext() ? ", " : ".");
        }
        return txt;
    }

    private static Text createToggleableEntityName(HighlightedEntity entity)
    {
        return FHUtils.colorText(entity.getName(), entity.getColor())
                .styled(style -> style
                        .withStrikethrough(!entity.isEnabled())
                        .withHoverEvent(CommandUtils.createToolTip(Text.literal(entity.getName() + " is ").append(FHUtils.getMessageWithConnotation("ENABLED", "DISABLED", entity.isEnabled()).append(" | Click to " + (entity.isEnabled() ? "disable" : "enable")))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh toggle entity \""+entity.getName()+"\"")));
    }
}
