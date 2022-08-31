package mod.icy_turtle.friendhighlighter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.util.CommandUtils;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to allow and suggest strings from the given list.
 */
public class StringListArgumentType implements ArgumentType<String>
{
    private final Collection<String> examples;
    /**
     * If the list changes overtime (ei. entities in the world), this supplies the list to suggest and test against.
     */
    private final Supplier<List<String>> listSupplier;
    /**
     * Passing in what was typed to cause the exception, what should be displayed when arg is invalid.
     */
    private final Function<String, Text> exceptionFunction;

    /**
     * Whether when suggesting the strings should be enclosed in quotes. Default is false.
     */
    private boolean suggestWithQuotes = false;

    /**
     * Constructor to specify the list, exception, and examples.
     * @param listSupplier the supplier to get the list to use for suggestions and parsing.
     * @param exceptionFunction function to create an exception message by passing in the problem string.
     * @param examples if there are examples of what your strings look like.
     * @see #StringListArgumentType(Supplier, Collection)
     */
    public StringListArgumentType(Supplier<List<String>> listSupplier, Function<String, Text> exceptionFunction, Collection<String> examples)
    {
        this.listSupplier = listSupplier;
        this.exceptionFunction = exceptionFunction;
        this.examples = examples;
    }

    /**
     * Works just like {@link #StringListArgumentType(Supplier, Function, Collection)}, but with a default exception.
     * @see #StringListArgumentType(Supplier, Function, Collection)
     */
    public StringListArgumentType(Supplier<List<String>> listSupplier, Collection<String> examples)
    {
        this(listSupplier, str -> Text.of("\"" + str + "\" is not one of " + listSupplier.toString()), examples);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        var name = CommandUtils.readQuotedOrUnquotedString(reader);
        if(listSupplier.get().contains(name))
            return name;
        throw new SimpleCommandExceptionType(exceptionFunction.apply(name)).createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        List<String> list = listSupplier.get();
        if(suggestWithQuotes)
            list = FHUtils.surroundListItemsWithQuotes(list);
        return CommandSource.suggestMatching(list, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }

    /**
     * Sets whether when suggesting, quotes should be added to enclose the string
     * @param suggestWithQuotes whether to add the quotes or not
     * @return this instance.
     */
    public StringListArgumentType setSuggestWithQuotes(boolean suggestWithQuotes)
    {
        this.suggestWithQuotes = suggestWithQuotes;
        return this;
    }
}