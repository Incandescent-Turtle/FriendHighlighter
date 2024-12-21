package mod.icy_turtle.friendhighlighter.command.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.command.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.command.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.command.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.command.arguments.StringListArgumentType;
import mod.icy_turtle.friendhighlighter.config.*;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AddEntityCommand extends Command
{

    private static final String ENTITY_NAME = "entityName",
            COLOR = "color";

    public AddEntityCommand(CommandHandler cmdHandler)
    {
        super(cmdHandler);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
    {
        // boolean arguments are optional and default to false
        return literal("addEntity")
                .then(CommandUtils.createEntityArgument(ENTITY_NAME)
                        .executes(this::addEntity)
                        .then(argument(COLOR, new ColorArgumentType())
                        .executes(this::addEntity)));
    }

    private int addEntity(CommandContext<FabricClientCommandSource> context)
    {
        var entityMap = FriendsListHandler.getEntityMap();

        String entityName = context.getArgument(ENTITY_NAME, String.class);
        String color = CommandUtils.getArgumentFromContext(context, COLOR, "#" + Integer.toHexString(FHSettings.getSettings().defaultColor));

        MutableText txt = Text.literal("");
        if(!entityMap.containsKey(entityName))
        {
            txt.append(entityName).append(" ").append(FHUtils.getPositiveMessage("ADDED")).append(" ").append("with color of").append(" ").append(FHUtils.colorText(color, FHUtils.hexToRGB(color)));
        } else
        {
            txt = FHUtils.getPositiveMessage(entityName + " Updated");
        }
        entityMap.put(entityName, new HighlightedEntity(entityName, FHUtils.hexToRGB(color)));
        cmdHandler.updateLists();
        FriendHighlighter.sendMessage(txt);
        FHConfig.saveConfig();
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
