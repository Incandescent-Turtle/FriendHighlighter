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
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A custom argument that replaces <b>true</b> and <b>false</b> with custom strings. Still allows for 'true' and 'false.'
 */
public class BooleanWithWords implements ArgumentType<Boolean>
{
    private final String trueStr, falseStr;

    public BooleanWithWords(String trueStr, String falseStr)
    {
        this.falseStr = falseStr;
        this.trueStr = trueStr;
    }

    @Override
    public Boolean parse(StringReader reader) throws CommandSyntaxException
    {
        String arg = CommandUtils.readSpacelessArgument(reader);
        if(arg.equals(trueStr) || arg.equalsIgnoreCase("true"))
            return true;
        if(arg.equals(falseStr) || arg.equalsIgnoreCase("false"))
            return false;
        throw new SimpleCommandExceptionType(Text.literal("Invalid entry, please type either " + trueStr + " or " + falseStr)).createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(List.of(trueStr, falseStr), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return List.of(trueStr, falseStr);
    }
}