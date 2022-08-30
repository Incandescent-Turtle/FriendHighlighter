package mod.icy_turtle.friendhighlighter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.commands.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.commands.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.ExistingFriendArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.CommandUtils;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

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
                    .then(argument("friendName", new PossibleFriendNameArgumentType())
                            .then(argument("color", new ColorArgumentType())
                                    .then(argument("onlyPlayers", new BooleanWithWords("onlyPlayers", "allEntities"))
                                            .then(argument("outlineFriend", new BooleanWithWords("outlineFriend", "dontOutlineFriend"))
                                                    .executes(this::addFriend))
                                            .executes(this::addFriend))
                                    .executes(this::addFriend))))

                .then(literal("remove")
                        .then(argument("friendName", new ExistingFriendArgumentType())
                                .executes(this::removeFriend)))
                .then(literal("clear")
                        .executes(this::clearFriendsList))
                .then(literal("list")
                        .then(literal("advanced")
                                .executes(context -> updateList(true, true)))
                        .executes(context -> updateList(true,false))
                )
        );
    }

    private int addFriend(CommandContext<FabricClientCommandSource> context)
    {
        var friendsMap = FHConfig.getInstance().friendsMap;

        String friendName = context.getArgument("friendName", String.class);
        String color = context.getArgument("color", String.class);
        boolean onlyPlayer = CommandUtils.getArgumentFromContext(context, "onlyPlayers", true);
        boolean outlineFriend = CommandUtils.getArgumentFromContext(context, "outlineFriend", true);
        friendsMap.put(friendName, new HighlightedFriend(friendName, FHUtils.hexToRGB(color), onlyPlayer, outlineFriend));

        MutableText txt = Text.literal("");
        //  if a list exists, update the list
        if(friendsMap.containsKey(friendName))
        {
            txt.append(friendName).append(" ")
                    .append(FHUtils.getPositiveMessage("ADDED"))
                    .append(" ")
                    .append("with color of")
                    .append(" ")
                    .append(FHUtils.colorText(color, FHUtils.hexToRGB(color)));
        } else {
            txt = FHUtils.getPositiveMessage("Friend Updated");
        }
        updateList();
        MinecraftClient.getInstance().player.sendMessage(txt, true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private int removeFriend(CommandContext<FabricClientCommandSource> context)
    {
        var friendsMap = FHConfig.getInstance().friendsMap;
        String friendName = context.getArgument("friendName", String.class);
        friendsMap.remove(friendName);

        MutableText txt = Text.literal("");
        txt.append(friendName)
            .append(" ")
            .append(FHUtils.getNegativeMessage("REMOVED"))
            .append(" ")
            .append(Text.of("from friends list"));
        updateList();
        MinecraftClient.getInstance().player.sendMessage(txt, true);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private int clearFriendsList(CommandContext<FabricClientCommandSource> context)
    {
        var friendsMap = FHConfig.getInstance().friendsMap;
        friendsMap.clear();
        MinecraftClient.getInstance().player.sendMessage(FHUtils.getNegativeMessage("Cleared Friends List"), true);
        updateList();
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    /**
     * used to update the current list - whether that be simple or advanced
     * @return
     */
    public int updateList()
    {
        return updateList(false, usingAdvancedList);
    }

    private int updateList(boolean sendNow, boolean advanced)
    {
        usingAdvancedList = advanced;

        var hud = MinecraftClient.getInstance().inGameHud;
        var chatHud = hud.getChatHud();
        var messages = chatHud.messages;

        if(!sendNow && (lastList == null || !messages.contains(lastList)))
            return 1;

        //  if it doesn't exist, insert at the front of the list, else insert and prev. location
        int index = messages.contains(lastList) ? messages.indexOf(lastList) : 0;
        if(sendNow)
            index = 0;

        messages.remove(lastList);

        int creationTicks = (sendNow) ? hud.getTicks() : lastList.getCreationTick();
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
            txt.append(FHUtils.createCopyableText(FHUtils.colorText(friend.name, friend.color), true))
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
                    .append(FHUtils.createCopyableText(FHUtils.getBoldAndColored(friend.name, friend.color), true))
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
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh remove \"" + friend.name + "\"")));
        return button;
    }

    private static Text createFriendBooleans(HighlightedFriend friend)
    {
        Function<Boolean, HoverEvent> hoverEvent = bool -> {
            var txt = Text.literal("");
            if(bool)
            {
                txt.append(FHUtils.getPositiveMessage("True"));
            } else {
                txt.append(FHUtils.getNegativeMessage("False"));
            }
            txt.append(" | Click to toggle");
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, txt);
        };
        MutableText onlyPlayers = FHUtils.colorText("onlyPlayers", friend.onlyPlayers ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", "\"" + friend.name + "\"", FHUtils.rgbToHex(friend.color), ""+!friend.onlyPlayers, ""+friend.outlineFriend)))
                .withHoverEvent(hoverEvent.apply(friend.onlyPlayers)));
        MutableText outlineFriend = FHUtils.colorText("outlineFriend", friend.outlineFriend ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", "\"" + friend.name + "\"", String.format("#%06x", friend.color & 0xFFFFFF), ""+friend.onlyPlayers, ""+!friend.outlineFriend)))
                .withHoverEvent(hoverEvent.apply(friend.outlineFriend)));
        return Text.literal(" ↳ ").append(onlyPlayers).append(" | ").append(outlineFriend);
    }
}