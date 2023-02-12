package mod.icy_turtle.friendhighlighter.command;

import com.mojang.brigadier.CommandDispatcher;
import mod.icy_turtle.friendhighlighter.command.commands.AddFriendCommand;
import mod.icy_turtle.friendhighlighter.command.commands.ClearCommand;
import mod.icy_turtle.friendhighlighter.command.commands.RemoveCommand;
import mod.icy_turtle.friendhighlighter.command.commands.ToggleCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.AdvancedListCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.ListCommand;
import mod.icy_turtle.friendhighlighter.command.commands.list.SimpleListCommand;
import mod.icy_turtle.friendhighlighter.command.commands.settings.SettingsCommand;
import mod.icy_turtle.friendhighlighter.command.commands.settings.SettingsDisplayCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Registers all the commands for the mods and defines their functionalities.
 */
public class CommandHandler
{
    public final ChatMessage
            simpleListChatMsg = new ChatMessage(SimpleListCommand::createSimpleList),
            advancedListChatMsg = new ChatMessage(AdvancedListCommand::createAdvancedList),
            settingsChatMsg = new ChatMessage(SettingsDisplayCommand::createSettings);

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
    {
        dispatcher.register(literal("fh")
                //  toggles either the highlighter or an individual friend
                .then(new ToggleCommand(this).createCommand())
                //  adds a friend to the list.
                .then(new AddFriendCommand(this).createCommand())

                //  removes a friend from the list
                .then(new RemoveCommand(this).createCommand())

                //  removes all friends from the list
                .then(new ClearCommand(this).createCommand())

                //  sends a list containing the names of friends and (if advanced) other info about them
                .then(new ListCommand(this).createCommand())

                .then(new SettingsCommand(this).createCommand())
        );
    }

    public void updateLists()
    {
        simpleListChatMsg.updateContent();
        advancedListChatMsg.updateContent();
    }
}