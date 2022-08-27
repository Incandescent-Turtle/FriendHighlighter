package mod.icy_turtle.friendhighlighter.commands.arguments;

import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class ExistingFriendArgumentType extends StringListArgumentType
{
    public ExistingFriendArgumentType(Function<String, String> errorMsg)
    {
        super(() -> FHConfig.getInstance().friendsMap.keySet(),
                name -> Text.of(errorMsg.apply(name)),
                List.of("icy_turtle", "Player123", "Xx_Notch_xX"));
    }
}
