package mod.icy_turtle.friendhighlighter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.commands.argument.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.commands.argument.PlayerNameArgumentType;
import mod.icy_turtle.friendhighlighter.commands.argument.StringListArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


@SuppressWarnings("unchecked")
public class Commands
{
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) literal("fh")

                .then(literal("toggle")
                    .executes(ctx -> FriendHighlighter.toggleHighlight()))

                .then(literal("add")
                    .then(argument("playerName", new PlayerNameArgumentType())
                            .then(argument("color", new ColorArgumentType())
                                    .then(argument("onlyPlayers", BoolArgumentType.bool())
                                            .then(argument("outlineFriend",BoolArgumentType.bool())
                                                    .executes(Commands::addPlayer))
                                            .executes(Commands::addPlayer))
                                    .executes(Commands::addPlayer))))

                .then(literal("remove")
                        .then(argument("playerName", new StringListArgumentType(() -> FHConfig.getInstance().playerMap.keySet(), name -> Text.of(name + " cannot be removed as they are not on your friends list."), List.of("icy_turtle", "Player123", "Xx_Notch_xX")))
                                .executes(Commands::removePlayer)))
                .then(literal("clear")
                        .executes(Commands::clearList))
                .then(literal("list")
                        .executes(Commands::displayList))
        );
    }

    private static int addPlayer(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().playerMap;
        String playerName = context.getArgument("playerName", String.class);
        String color = context.getArgument("color", String.class);
        Boolean onlyPlayer;
        try {
            onlyPlayer = context.getArgument("onlyPlayers", Boolean.class);
        } catch(IllegalArgumentException e) {
            onlyPlayer = true;
        }
        Boolean outlineFriend;

        try {
            outlineFriend = context.getArgument("outlineFriend", Boolean.class);
        } catch(IllegalArgumentException e) {
            outlineFriend = true;
        }
        playerMap.put(playerName, new HighlightedFriend(playerName, FHUtils.hexToRGB(color), onlyPlayer, outlineFriend));
        MinecraftClient.getInstance().player.sendMessage(Texts.join(List.of(Text.of(playerName), FHUtils.getPositiveMessage("ADDED"), Text.of("with color of"), FHUtils.colorText(color, FHUtils.hexToRGB(color))), Text.of(" ")), true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private static int removePlayer(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().playerMap;
        String playerName = context.getArgument("playerName", String.class);
        playerMap.remove(playerName);
        MinecraftClient.getInstance().player.sendMessage(Texts.join(List.of(Text.of(playerName), FHUtils.getNegativeMessage("REMOVED"), Text.of("from friends list")), Text.of(" ")), true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private static int clearList(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().playerMap;
        playerMap.clear();
        MinecraftClient.getInstance().player.sendMessage(FHUtils.getNegativeMessage("Cleared Friends List"), true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private static int displayList(CommandContext<FabricClientCommandSource> context)
    {
        var player = MinecraftClient.getInstance().player;
        Text txt = Text.of("");
        var playerMap = FHConfig.getInstance().playerMap;
        for (var p : playerMap.values())
        {
            txt = Texts.join(List.of(txt, FHUtils.colorText(p.name, p.color), Text.of(", ")),Text.of(""));
        }
        player.sendMessage(txt);
        return 1;
    }

//    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
//        dispatcher.register(literal("antioch")
//                .then(required("location", BlockPosArgumentType.blockPos()
//                        .executes(ctx -> antioch(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "location")))))
//                .executes(ctx -> antioch(ctx.getSource(), null)));
//    }
}