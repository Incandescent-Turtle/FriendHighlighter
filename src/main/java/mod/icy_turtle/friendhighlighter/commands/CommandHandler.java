package mod.icy_turtle.friendhighlighter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.commands.arguments.BooleanWithWords;
import mod.icy_turtle.friendhighlighter.commands.arguments.ColorArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.PossibleFriendNameArgumentType;
import mod.icy_turtle.friendhighlighter.commands.arguments.StringListArgumentType;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.CommandUtils;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.BiFunction;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Registers all the commands for the mods and defines their functionalities.
 */
public class CommandHandler
{
    /**
     * Whether an updated list should be a simple or advanced list if not specified.
     */
    private boolean usingAdvancedList = false;

    /**
     * The {@link MutableText} that represents the list that is displayed in chat.
     */
    public final MutableText chatListText = Text.literal("");

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register(literal("fh")
                //  toggles either the highlighter or an individual friend
                .then(literal("toggle")
                        //  toggles a friend
                        .then(createExistingFriendArgument()
                                .executes(this::toggleFriend))
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
                        .then(createExistingFriendArgument()
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
        var friendsMap = FriendsListHandler.getFriendsMap();

        String friendName = context.getArgument("friendName", String.class);
        String color = context.getArgument("color", String.class);
        boolean onlyPlayer = CommandUtils.getArgumentFromContext(context, "onlyPlayers", true);
        boolean outlineFriend = CommandUtils.getArgumentFromContext(context, "outlineFriend", true);

        MutableText txt = Text.literal("");
        if(!friendsMap.containsKey(friendName))
        {
            txt.append(friendName).append(" ")
                    .append(FHUtils.getPositiveMessage("ADDED"))
                    .append(" ")
                    .append("with color of")
                    .append(" ")
                    .append(FHUtils.colorText(color, FHUtils.hexToRGB(color)));
        } else {
            txt = FHUtils.getPositiveMessage(friendName + " Updated");
        }
        friendsMap.put(friendName, new HighlightedFriend(friendName, FHUtils.hexToRGB(color), onlyPlayer, outlineFriend).setEnabled(friendsMap.getOrDefault(friendName,new HighlightedFriend()).isEnabled()));
        //  if a list exists, update the list
        updateList();
        FriendHighlighter.sendMessage(txt);
        FHConfig.saveConfig();
        return Command.SINGLE_SUCCESS;
    }

    private int toggleFriend(CommandContext<FabricClientCommandSource> context)
    {
        String friendName = context.getArgument("friendName", String.class);
        var friend = FriendsListHandler.getFriendsMap().get(friendName);
        if(friend != null)
        {
            friend.setEnabled(!friend.isEnabled());
            updateList();
            FriendHighlighter.sendMessage(Text.literal(friendName)
                            .append(" ")
                            .append(FHUtils.getMessageWithConnotation("ENABLED", "DISABLED", friend.isEnabled()))
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    private int removeFriend(CommandContext<FabricClientCommandSource> context)
    {
        var friendsMap = FriendsListHandler.getFriendsMap();
        String friendName = context.getArgument("friendName", String.class);
        friendsMap.remove(friendName);

        MutableText txt = Text.literal("");
        txt.append(friendName)
            .append(" ")
            .append(FHUtils.getNegativeMessage("REMOVED"))
            .append(" ")
            .append(Text.of("from friends list"));
        updateList();
        FriendHighlighter.sendMessage(txt);
        FHConfig.saveConfig();
        if(friendsMap.size() != 0)
            MinecraftClient.getInstance().inGameHud.getChatHud().scroll(-2);
        return Command.SINGLE_SUCCESS;
    }

    private int clearFriendsList(CommandContext<FabricClientCommandSource> context)
    {
        var friendsMap = FriendsListHandler.getFriendsMap();
        friendsMap.clear();
        FriendHighlighter.sendMessage(FHUtils.getNegativeMessage("Cleared Friends List"));
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
     * @param sendNow whether the list should be sent now, or just inserted in the position of {@link #chatListText}.
     * @param advanced whether the list should be advanced or simple
     * @return {@link Command#SINGLE_SUCCESS}
     */
    private int updateList(boolean sendNow, boolean advanced)
    {
        usingAdvancedList = advanced;

        var hud = MinecraftClient.getInstance().inGameHud;
        var chatHud = hud.getChatHud();

        if(sendNow)
        {
            chatHud.messages.removeIf(line -> line.content().equals(chatListText));
            //  has to happen after old one is removed ^
            setChatListText(advanced ? getAdvancedList() : getSimpleList());
            MinecraftClient.getInstance().player.sendMessage(chatListText);
        } else {
            setChatListText(advanced ? getAdvancedList() : getSimpleList());
        }
        // reloading chat and keeping scroll place in chat
        var lines = chatHud.scrolledLines;
        FriendHighlighter.logChatMessages = false;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
        FriendHighlighter.logChatMessages = true;
        chatHud.scroll(lines);
        return 1;
    }

    private MutableText getSimpleList()
    {
        MutableText txt = Text.literal("").append("Friends List: ");
        var map = FriendsListHandler.getFriendsMap();
        if(map.size() == 0)
            return txt.append(" Empty.");
        var itr = map.entrySet().iterator();
        while(itr.hasNext())
        {
            var friend = itr.next().getValue();
            txt.append(createToggleableName(friend))
                    .append(itr.hasNext() ? ", " : ".");
        }
        return txt;
    }

    public static MutableText getAdvancedList()
    {
        var map = FriendsListHandler.getFriendsMap();
        MutableText txt = Text.literal("");
        txt.append(Text.literal("\nFriends List").styled(style -> style.withUnderline(true)));
        if(map.size() == 0)
            return txt.append(" - Empty\n");
        else
            txt.append(" | ").append(createToggleButton()).append("\n\n");
        for(var friend : map.values())
        {
            txt.append(
                    createDeleteButton(friend))
                    .append(" ")
                    .append(createToggleableName(friend))
                    .append("\n")
                    .append(createFriendBooleans(friend))
                    .append("\n");
        }
        txt.append("______\n")
                .append(createClearButton())
                .append("\n");
        return txt;
    }

    private static Text createToggleableName(HighlightedFriend friend)
    {
        return FHUtils.colorText(friend.name, friend.color)
                .styled(style -> style
                        .withStrikethrough(!friend.isEnabled())
                        .withHoverEvent(createToolTip(Text.literal(friend.name + " is ").append(FHUtils.getMessageWithConnotation("ENABLED", "DISABLED", friend.isEnabled()).append(" | Click to " + (friend.isEnabled() ? "disable" : "enable")))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh toggle \""+friend.name+"\"")));
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
                .withHoverEvent(createToolTip("Remove " + friend.name + " from friends list"))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh remove \"" + friend.name + "\"")));
    }

    /**
     * Creates a clickable {@link Text} that toggles the friend highlighter.
     * @return the toggle button.
     */
    private static Text createToggleButton()
    {
        return FHUtils.getMessageWithConnotation("Enabled", "Disabled", FriendHighlighter.isHighlighterEnabled)
                .styled(style -> style
                        .withHoverEvent(createToolTip("Click to toggle Friend Highlighter"))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh toggle")));
    }

    /**
     * Creates a clickable {@link Text} that suggests the clear command
     * @return the clear button.
     */
    private static Text createClearButton()
    {
        return FHUtils.getNegativeMessage("Clear")
                .styled(style -> style
                        .withHoverEvent(createToolTip("Clear Friends List"))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fh clear")));
    }

    /**
     * Creates the text that displays the values of the boolean values of a {@link HighlightedFriend} with toggle capabilities.
     * @param friend the friend of which the boolean refer.
     * @return the booleans to display
     */
    private static Text createFriendBooleans(HighlightedFriend friend)
    {
        //  click event to send a chat message to toggle the booleans
        BiFunction<Boolean, Boolean, ClickEvent> clickEvent = (onlyPlayers, outlineFriend) -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fh add " + String.join(" ", "\"" + friend.name + "\"", FHUtils.rgbToHex(friend.color), ""+onlyPlayers, ""+outlineFriend));
        MutableText onlyPlayers = FHUtils.colorText(friend.onlyPlayers ? "Only Players" : "All Entities", friend.onlyPlayers ? 0xA7C7E7 : 0xFF5F1F)
                .styled(style -> style
                .withClickEvent(clickEvent.apply(!friend.onlyPlayers, friend.outlineFriend))
                .withHoverEvent(createToolTip("Click to change to " + (!friend.onlyPlayers ? "only players" : "all entities"))));

        MutableText outlineFriend = FHUtils.colorText("Outline Friend", friend.outlineFriend ? Formatting.GREEN : Formatting.RED)
                .styled(style -> style
                .withClickEvent(clickEvent.apply(friend.onlyPlayers, !friend.outlineFriend))
                .withHoverEvent(
                        createToolTip(FHUtils.getMessageWithConnotation("TRUE", "FALSE", friend.outlineFriend)
                        .append(" | Click to toggle"))
                ));

        return Text.literal(" ↳ ").append(onlyPlayers).append(" | ").append(outlineFriend);
    }

    /**
     * Creates a @{link StringListArgumentType} using the friends list to populate the list.
     * @return an argument that will supply and restrict input to the friends list at time of usage.
     */
    private RequiredArgumentBuilder<FabricClientCommandSource, String> createExistingFriendArgument()
    {
        return argument("friendName",
                new StringListArgumentType(
                    () -> FriendsListHandler.getFriendsMap().keySet().stream().toList(),
                    name -> Text.literal(name + " cannot be removed as they are not on your friends list."),
                    List.of("icy_turtle", "Player123", "Xx_Notch_xX")
                ).setSuggestWithQuotes(true)
        );
    }

    /**
     * Replaces the current list in the chat with the text passed in.
     * @param text the text to represent the new list.
     */
    public void setChatListText(MutableText text)
    {
        chatListText.getSiblings().clear();
        chatListText.getSiblings().addAll(text.getSiblings());
    }

    /**
     * Creates a {@link HoverEvent} tooltip for chat messages.
     * @param txt the tooltip to be displayed.
     * @return the event to be used to create the desired tooltip.
     * @see #createToolTip(String)
     */
    private static HoverEvent createToolTip(MutableText txt)
    {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, FHSettings.getSettings().toolTipsEnabled ? txt : null);
    }

    /**
     * Creates a {@link HoverEvent} tooltip for chat messages.
     * @param str the tooltip to be displayed.
     * @return the event to be used to create the desired tooltip.
     * @see #createToolTip(MutableText)
     */
    private static HoverEvent createToolTip(String str)
    {
        return createToolTip(Text.literal(str));
    }
}