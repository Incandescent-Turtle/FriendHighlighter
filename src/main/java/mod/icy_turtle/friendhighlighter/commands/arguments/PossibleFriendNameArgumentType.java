package mod.icy_turtle.friendhighlighter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.util.CommandUtils;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * An argument that represents the name of a possible entity/player, allowing for quotes, with suggestions of players, friends, and closeby entities.
 */
public class PossibleFriendNameArgumentType implements ArgumentType<String>
{
    private static final Collection<String> examples = List.of(
            "icy_turtle",
            "dog_lover123",
            "Player123"
    );

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        return CommandUtils.readQuotedOrUnquotedString(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        //  adding player names
        List<String> nameList = MinecraftClient.getInstance().world.getPlayers().stream().map(p -> p.getName().getString()).collect(
                Collectors.toList());
        //  removes the player from their own list
        nameList.remove(MinecraftClient.getInstance().player.getName().getString());

        //  adding close/loaded entities
        for (var entity : MinecraftClient.getInstance().world.getEntities())
        {
            if(entity.hasCustomName())
                nameList.add(entity.getName().getString());
        }

        //  add already existing friends for editing
        nameList.addAll(FriendsListHandler.getFriendsMap().keySet());
        nameList = FHUtils.surroundListItemsWithQuotes(nameList);
        return CommandSource.suggestMatching(nameList, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }
}