package mod.icy_turtle.friendhighlighter.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.util.FHColor;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A custom color argument that allows selection from a
 */
public class ColorArgumentType implements ArgumentType<String>
{
    private static final Collection<String> examples = List.of(
            "red",
            "blue",
            "Black",
            "#FFA500",
            "#2300aa"
    );

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        String color = CommandUtils.readSpacelessArgument(reader);

        //  if the name of a color, return the hex for that color
        if(FHColor.COLOR_MAP.containsKey(color.toLowerCase()))
            return FHColor.getHexFromColorName(color);

        if(color.charAt(0) == '#')
        {
            if(FHUtils.isValidHexCode(color))
                return color;
            else
                throw new SimpleCommandExceptionType(Text.literal("Invalid color. Valid hex codes are 1-6 digits comprising of 0-9 and A-F.")).createWithContext(reader);
        }
        throw new SimpleCommandExceptionType(Text.literal("Invalid color. Use a pre-programmed colour or a hex code starting with #.")).createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(FHColor.COLOR_MAP.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return examples;
    }
}