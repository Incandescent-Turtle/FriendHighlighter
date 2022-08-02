package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ModUtils
{
	private ModUtils(){}

	public static List<Text> getBoldAndColored(String msg, Formatting color)
	{
		return Text.of(msg).getWithStyle(Style.EMPTY.withBold(true).withColor(color));
	}
}