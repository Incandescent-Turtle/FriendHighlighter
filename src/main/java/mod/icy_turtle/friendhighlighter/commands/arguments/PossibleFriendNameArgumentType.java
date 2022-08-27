package mod.icy_turtle.friendhighlighter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.commands.CommandHandler;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        String name = CommandHandler.getArgumentFromReader(reader);
        if(name.length() > 16)
        {
            throw new SimpleCommandExceptionType(Text.literal("Invalid username: Invalid length. Must be <=16 characters")).createWithContext(reader);
        }
        return name;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        Set<String> nameList = MinecraftClient.getInstance().world.getPlayers().stream().map(p -> p.getDisplayName().getString()).collect(Collectors.toSet());
        //  removes the player from their own list
        nameList.remove(MinecraftClient.getInstance().player.getDisplayName().getString());

        for (var entity : MinecraftClient.getInstance().world.getEntities())
        {
            if(entity.hasCustomName())
                nameList.add(entity.getDisplayName().getString());
        }

        //  add already existing friends for editing
        for (var friend : FHConfig.getInstance().friendsMap.keySet())
        {
            nameList.add(friend);
        }

            return CommandSource.suggestMatching(nameList, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }
}