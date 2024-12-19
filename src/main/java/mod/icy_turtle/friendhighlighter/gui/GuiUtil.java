package mod.icy_turtle.friendhighlighter.gui;

import mod.icy_turtle.friendhighlighter.config.FHSettings;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

public class GuiUtil
{
	private GuiUtil(){}

	protected static Supplier<Optional<Text[]>> createToolTip(String str)
	{
		if(FHSettings.getSettings().tooltipsEnabled)
			return () -> Optional.of(new Text[]{Text.literal(FHUtils.splitEveryNCharacters(str, 20))});
		return Optional::empty;
	}
}
