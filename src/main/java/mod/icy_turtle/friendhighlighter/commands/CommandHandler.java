package mod.icy_turtle.friendhighlighter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.commands.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.commands.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.ExistingFriendArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class CommandHandler
{
    private ChatHudLine<Text> lastList;
    private boolean usingAdvancedList = false;

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register(literal("fh")

                .then(literal("toggle")
                    .executes(ctx -> FriendHighlighter.toggleHighlight()))

                .then(literal("add")
                    .then(argument("playerName", new PossibleFriendNameArgumentType())
                            .then(argument("color", new ColorArgumentType())
                                    .then(argument("onlyPlayers", new BooleanWithWords("onlyPlayers", "allEntities"))
                                            .then(argument("outlineFriend", new BooleanWithWords("outlineFriend", "dontOutlineFriend"))
                                                    .executes(this::addPlayer))
                                            .executes(this::addPlayer))
                                    .executes(this::addPlayer))))

                .then(literal("remove")
                        .then(argument("playerName", new ExistingFriendArgumentType(name -> name + " cannot be removed as they are not on your friends list."))
                                .executes(this::removePlayer)))
                .then(literal("clear")
                        .executes(this::clearFriendsList))
                .then(literal("list")
                        .then(literal("advanced")
                                .executes(context -> updateList(true,true, true)))
                        .executes(context -> updateList(true,true, false))
                )
        );
    }

    private int addPlayer(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().friendsMap;

        String playerName = context.getArgument("playerName", String.class);
        String color = context.getArgument("color", String.class);
        boolean onlyPlayer = getArgumentFromContext(context, "onlyPlayers", true);
        boolean outlineFriend = getArgumentFromContext(context, "outlineFriend", true);

        playerMap.put(playerName, new HighlightedFriend(playerName, FHUtils.hexToRGB(color), onlyPlayer, outlineFriend));


        MutableText txt = Text.literal("");
        var messages = MinecraftClient.getInstance().inGameHud.getChatHud().messages;
        //  if a list exists, update the list
        if(lastList != null && messages.contains(lastList))
        {
            txt = FHUtils.getPositiveMessage("Friend Updated");
            //  edits existing list
            updateList();
        } else {
            txt.append(playerName).append(" ")
                    .append(FHUtils.getPositiveMessage("ADDED"))
                    .append(" ")
                    .append("with color of")
                    .append(" ")
                    .append(FHUtils.colorText(color, FHUtils.hexToRGB(color)));
        }
        MinecraftClient.getInstance().player.sendMessage(txt, true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private int removePlayer(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().friendsMap;
        String playerName = context.getArgument("playerName", String.class);
        playerMap.remove(playerName);

        var messages = MinecraftClient.getInstance().inGameHud.getChatHud().messages;
        if(lastList != null && messages.contains(lastList))
        {
            updateList();
        }

        MutableText txt = Text.literal("");
        txt.append(playerName)
            .append(" ")
            .append(FHUtils.getNegativeMessage("REMOVED"))
            .append(" ")
            .append(Text.of("from friends list"));
        MinecraftClient.getInstance().player.sendMessage(txt, true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private int clearFriendsList(CommandContext<FabricClientCommandSource> context)
    {
        var playerMap = FHConfig.getInstance().friendsMap;
        playerMap.clear();
        MinecraftClient.getInstance().player.sendMessage(FHUtils.getNegativeMessage("Cleared Friends List"), true);
        updateList();
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    public int updateList()
    {
        return updateList(false, false);
    }

    public int updateList(boolean sendNow, boolean createNewMessage)
    {
        return updateList(sendNow, createNewMessage, usingAdvancedList);
    }

    private int updateList(boolean sendNow, boolean createNewMessage, boolean advanced)
    {
        usingAdvancedList = advanced;

        if(sendNow)
            createNewMessage = true;

        var hud = MinecraftClient.getInstance().inGameHud;
        var chatHud = hud.getChatHud();
        var messages = chatHud.messages;

        if(!createNewMessage && (lastList == null || !messages.contains(lastList)))
            return 1;

        //  if it doesnt exist, insert at the front of the list, else insert and prev. location
        int index = messages.contains(lastList) ? messages.indexOf(lastList) : 0;
        if(sendNow)
            index = 0;

        messages.remove(lastList);

        int creationTicks = (sendNow || createNewMessage) ? hud.getTicks() : lastList.getCreationTick();
        lastList = new ChatHudLine<>(creationTicks, advanced ? getAdvancedList() : getSimpleList(), 0);
        messages.add(index, lastList);

        // reloading chat and keeping scroll place in chat
        var lines = chatHud.scrolledLines;
        chatHud.reset();
        chatHud.scroll(lines);
        return 1;
    }

    private static Text getSimpleList()
    {
        MutableText txt = Text.literal("Friend's List: ");
        var map = FHConfig.getInstance().friendsMap;
        if(map.size() == 0)
            return txt.append(" Empty.");
        var itr = map.entrySet().iterator();
        while(itr.hasNext())
        {
            var friend = itr.next().getValue();
            txt.append(FHUtils.createCopyableText(FHUtils.colorText(friend.name, friend.color)))
                    .append(itr.hasNext() ? ", " : ".");
        }
        return txt;
    }

    private static Text getAdvancedList()
    {
        var map = FHConfig.getInstance().friendsMap;
        MutableText txt = Text.literal("");
        txt.append(Text.literal("Friend's List").styled(style -> style.withUnderline(true)));
        if(map.size() == 0)
            return txt.append(" - Empty");
        else
            txt.append("\n\n");
        var itr = map.entrySet().iterator();
        while(itr.hasNext())
        {
            var friend = itr.next().getValue();
            txt.append(createDeleteButton(friend))
                    .append(" ")
                    .append(FHUtils.createCopyableText(FHUtils.getBoldAndColored(friend.name, friend.color)))
                    .append("\n")
                    .append(createFriendBooleans(friend))
                    .append(itr.hasNext() ? "\n" : "");
        }
        return txt;
    }

    private static Text createDeleteButton(HighlightedFriend friend)
    {
        Text button = Text.literal("✖").styled(style -> style
                .withColor(Formatting.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Remove Friend")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh remove " + friend.name)));
        return button;
    }

    private static Text createFriendBooleans(HighlightedFriend friend)
    {
        var hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to toggle"));
        MutableText onlyPlayers = FHUtils.colorText("onlyPlayers", friend.onlyPlayers ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", friend.name, String.format("#%06x", friend.color & 0xFFFFFF), ""+!friend.onlyPlayers, ""+friend.outlineFriend)))
                .withHoverEvent(hoverEvent));
        MutableText outlineFriend = FHUtils.colorText("outlineFriend", friend.outlineFriend ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", friend.name, String.format("#%06x", friend.color & 0xFFFFFF), ""+friend.onlyPlayers, ""+!friend.outlineFriend)))
                .withHoverEvent(hoverEvent));
        return Text.literal(" ↳ ").append(onlyPlayers).append(" | ").append(outlineFriend);
    }

    public static String getArgumentFromReader(StringReader reader)
    {
        int argBeginning = reader.getCursor();

        if (!reader.canRead())
            reader.skip();

        while(reader.canRead() && reader.peek() != ' ')
            reader.skip();

        return reader.getString().substring(argBeginning, reader.getCursor());
    }

    private static <T> T getArgumentFromContext(CommandContext<FabricClientCommandSource> context, String name, T defaultValue)
    {
        T val;
        try {
            val = (T) context.getArgument(name, defaultValue.getClass());
        } catch(Exception e) {
            val = defaultValue;
        }
        return val;
    }
}