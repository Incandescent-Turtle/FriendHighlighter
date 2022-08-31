package mod.icy_turtle.friendhighlighter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.commands.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.commands.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.StringListArgumentType;
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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Registers all the commands for the mods and defines their functionalities.
 */
public class CommandHandler
{
    /**
     * The last list that was sent in the chat.
     */
    private ChatHudLine<Text> lastList;
    /**
     * Whether an updated list should be a simple or advanced list if not specified.
     */
    private boolean usingAdvancedList = false;

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register(literal("fh")
                //  turns the highlighter on and off
                .then(literal("toggle")
                    .executes(ctx -> FriendHighlighter.toggleHighlight()))

                //  adds a friend to the list.
                // boolean arguments are optional and default to false
                .then(literal("add")
                    .then(argument("friendName", new PossibleFriendNameArgumentType())
                            .then(argument("color", new ColorArgumentType())
                                    .then(argument("onlyPlayers", new BooleanWithWords("onlyPlayers", "allEntities"))
                                            .then(argument("outlineFriend", new BooleanWithWords("outlineFriend", "dontOutlineFriend"))
                                                    .executes(this::addFriend))
                                            .executes(this::addFriend))
                                    .executes(this::addFriend))))

                //  removes a friend from the list
                .then(literal("remove")
                        .then(argument("friendName", createExistingFriendArgumentType())
                                .executes(this::removeFriend)))
                //  removes all friends from the list
                .then(literal("clear")
                        .executes(this::clearFriendsList))
                //  sends a list containing the names of friends and (if advanced) other info about them
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
     * Used to update the current list - whether that be simple or advanced.
     * @return {@link Command#SINGLE_SUCCESS}
     * @see #updateList()
     */
    public int updateList()
    {
        return updateList(false, usingAdvancedList);
    }

    /**
     *
     * @param sendNow whether the list should be sent now, or just inserted in the position of {@link #lastList}.
     * @param advanced whether the list should be advanced or simple
     * @return {@link Command#SINGLE_SUCCESS}
     */
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

    /**
     * Creates a character that deletes the given friend when clicked.
     * @param friend the friend to delete when clicked.
     * @return the delete button.
     */
    private static Text createDeleteButton(HighlightedFriend friend)
    {
        return Text.literal("✖").styled(style -> style
                .withColor(Formatting.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Remove Friend")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh remove \"" + friend.name + "\"")));
    }

    /**
     * Creates the text that displays the values of the boolean values of a {@link HighlightedFriend} with toggle capabilities.
     * @param friend the friend of which the boolean refer.
     * @return the booleans to display
     */
    private static Text createFriendBooleans(HighlightedFriend friend)
    {
        //  hover event to display "true" or "false" - mostly for colorblind people
        Function<Boolean, HoverEvent> hoverEvent = enabled -> {
            var txt = Text.literal("");
            txt.append(FHUtils.getMessageWithConnotation("TRUE", "FALSE", enabled));
            txt.append(" | Click to toggle");
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, txt);
        };
        //  click event to send a chat message to toggle the booleans
        BiFunction<Boolean, Boolean, ClickEvent> clickEvent = (onlyPlayers, outlineFriend) -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", "\"" + friend.name + "\"", FHUtils.rgbToHex(friend.color), ""+onlyPlayers, ""+outlineFriend));

        MutableText onlyPlayers = FHUtils.colorText("onlyPlayers", friend.onlyPlayers ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(clickEvent.apply(!friend.onlyPlayers, friend.outlineFriend))
                .withHoverEvent(hoverEvent.apply(friend.onlyPlayers)));

        MutableText outlineFriend = FHUtils.colorText("outlineFriend", friend.outlineFriend ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(clickEvent.apply(friend.onlyPlayers, !friend.outlineFriend))
                .withHoverEvent(hoverEvent.apply(friend.outlineFriend)));

        return Text.literal(" ↳ ").append(onlyPlayers).append(" | ").append(outlineFriend);
    }

    /**
     * Creates a @{link StringListArgumentType} using the friends list to populate the list.
     * @return an argument that will supply and restrict input to the friends list at time of usage.
     */
    private StringListArgumentType createExistingFriendArgumentType()
    {
        return new StringListArgumentType(
                () -> FHConfig.getInstance().friendsMap.keySet().stream().toList(),
                name -> Text.of(name + " cannot be removed as they are not on your friends list."),
                List.of("icy_turtle", "Player123", "Xx_Notch_xX")).setSuggestWithQuotes(true);
    }
}