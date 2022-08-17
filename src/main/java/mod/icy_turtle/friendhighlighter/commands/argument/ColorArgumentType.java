package mod.icy_turtle.friendhighlighter.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.icy_turtle.friendhighlighter.util.FHColor;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        while (reader.canRead() && reader.peek() != ' ')
        {
            reader.skip();
        }

        String color = reader.getString().substring(argBeginning, reader.getCursor());
        String hexFromName = FHColor.getHex(color);
        System.out.println(color);
        if(hexFromName != null)
            return hexFromName;

        if(color.charAt(0) == '#')
        {
            if(color.matches("^#([0-9A-Fa-f]{1,6})$"))
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