package mod.icy_turtle.friendhighlighter.commands.arguments;

import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class ExistingFriendArgumentType extends StringListArgumentType
{
    public ExistingFriendArgumentType()
    {
        super(
                () -> FHConfig.getInstance().friendsMap.keySet(),
                () -> FHConfig.getInstance().friendsMap.keySet().stream().map(name -> "\""+name+"\"").collect(Collectors.toList()),
                name -> Text.of(name + " cannot be removed as they are not on your friends list."),
                List.of("icy_turtle", "Player123", "Xx_Notch_xX"));
    }
}
