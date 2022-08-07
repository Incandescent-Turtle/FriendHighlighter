package mod.icy_turtle.friendhighlighter.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerNameArgumentType implements ArgumentType<String>
{
    private static final Collection<String> examples = List.of(
            "icy_turtle",
            "dog_lover123",
            "Player123"
    );

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        String name = reader.readUnquotedString();
        if(name.length() < 3 || name.length() > 16)
        {
            throw new SimpleCommandExceptionType(Text.literal("Invalid username: Invalid length. Must be between 3-16 characters")).createWithContext(reader);
        }
        if(!name.matches("\\w+"))
        {
            throw new SimpleCommandExceptionType(Text.literal("Invalid username: Illegal characters. Valid characters are letters, numbers, and underscores.")).createWithContext(reader);
        }
        return name;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        if (context.getSource() instanceof CommandSource)
        {
            StringReader stringReader = new StringReader(builder.getInput());
            stringReader.setCursor(builder.getStart());

            List<String> collection = MinecraftClient.getInstance().world.getPlayers().stream().map(p -> p.getDisplayName().getString()).collect(Collectors.toList());
            return CommandSource.suggestMatching(collection, builder);
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }
}