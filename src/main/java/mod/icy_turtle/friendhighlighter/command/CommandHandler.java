package mod.icy_turtle.friendhighlighter.command;

import com.mojang.brigadier.CommandDispatcher;
import mod.icy_turtle.friendhighlighter.command.commands.*;
import mod.icy_turtle.friendhighlighter.command.commands.list.AdvancedListFriendsCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.ListFriendsCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.SimpleListEntitiesCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.SimpleListFriendsCommand;
import mod.icy_turtle.friendhighlighter.command.commands.settings.SettingsCommand;
import mod.icy_turtle.friendhighlighter.command.commands.settings.SettingsDisplayCommand;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Registers all the commands for the mods and defines their functionalities.
 */
public class CommandHandler
{
    public final ChatMessage
            simpleFriendsListChatMsg = new ChatMessage(SimpleListFriendsCommand::createSimpleFriendsList),
            advancedFriendsListChatMsg = new ChatMessage(AdvancedListFriendsCommand::createAdvancedFriendsList),
            settingsChatMsg = new ChatMessage(SettingsDisplayCommand::createSettings),
            simpleEntityListChatMsg = new ChatMessage(SimpleListEntitiesCommand::createSimpleEntityList);

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register(literal("fh")
                //  toggles either the highlighter or an individual friend
                .then(new ToggleCommand(this).createCommand())

                //  adds a friend to the list.
                .then(new AddFriendCommand(this).createCommand())

                // adds a new entity to the entity list
                .then(new AddEntityCommand(this).createCommand())

                // removes friend from friendlist
                .then(literal("removeFriend")
                        .then(CommandUtils.createExistingFriendArgument("friendName")
                                .executes(ctx -> CommandUtils.removeFromList(ctx, "friendName", this, FriendsListHandler.getFriendsMap()))
                        ))

                // removes entity from entity list
                .then(literal("removeEntity")
                        .then(CommandUtils.createExistingEntityArgument("entityName")
                                .executes(ctx -> CommandUtils.removeFromList(ctx, "entityName", this, FriendsListHandler.getEntityMap()))
                        ))


        //  removes all friends from the list
                .then(new ClearCommand(this).createCommand())

                //  sends a list containing the names of friends and (if advanced) other info about them
                .then(new ListFriendsCommand(this).createCommand())

                // sends a list contains the entities that are being highlighted
                .then(new SimpleListEntitiesCommand(this).createCommand())

                .then(new SettingsCommand(this).createCommand())
        );
    }

    public void updateLists()
    {
        simpleFriendsListChatMsg.updateContent();
        advancedFriendsListChatMsg.updateContent();
        settingsChatMsg.updateContent();
        simpleEntityListChatMsg.updateContent();
    }
}