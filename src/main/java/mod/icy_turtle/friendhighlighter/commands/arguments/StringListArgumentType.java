package mod.icy_turtle.friendhighlighter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.util.CommandUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class StringListArgumentType implements ArgumentType<String>
{
    private final Collection<String> examples;
    private final Supplier<Collection<String>> listSupplier;
    private final Supplier<Collection<String>> suggestionSupplier;
    private final Function<String, Text> exceptionFunction;

    public StringListArgumentType(Supplier<Collection<String>> listSupplier, Supplier<Collection<String>> suggestionSupplier, Function<String, Text> exceptionFunction, Collection<String> examples)
    {
        this.listSupplier = listSupplier;
        this.exceptionFunction = exceptionFunction;
        this.examples = examples;
        this.suggestionSupplier = suggestionSupplier;
    }

    public StringListArgumentType(Supplier<Collection<String>> listSupplier, Supplier<Collection<String>> suggestionSupplier, Collection<String> examples)
    {
        this(listSupplier, suggestionSupplier, str -> Text.of("\"" + str + "\" is not one of " + listSupplier.toString()), examples);
    }

    public StringListArgumentType(Supplier<Collection<String>> listSupplier, Function<String, Text> exceptionFunction, Collection<String> examples)
    {
        this(listSupplier, listSupplier, exceptionFunction, examples);
    }

    public StringListArgumentType(Supplier<Collection<String>> listSupplier, Collection<String> examples)
    {
        this(listSupplier, listSupplier, examples);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
//        String name = Commands.getArgument(reader);
        var name = CommandUtils.readQuotedString(reader);
        if(listSupplier.get().contains(name))
            return name;
        throw new SimpleCommandExceptionType(exceptionFunction.apply(name)).createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        Collection<String> collection = suggestionSupplier.get();
        return CommandSource.suggestMatching(collection, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }
}