package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class FHUtils
{
	private FHUtils(){}

	public static List<Text> getBoldAndColored(String msg, Formatting color)
	{
		return Text.of(msg).getWithStyle(Style.EMPTY.withBold(true).withColor(color));
	}

	public static Text color(String str, int rgb)
	{
		return Text.of(str).getWithStyle(Style.EMPTY.withColor(rgb)).get(0);
	}

	public static Text getPositiveMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.GREEN).get(0);
	}

	public static Text getNegativeMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.RED).get(0);
	}

	public static int hexToRGB(String hex)
	{
		hex += "0".repeat(7 - hex.length());
		hex = hex.toUpperCase();
		int r = Integer.parseInt(hex.substring(1, 3), 16);
		int g = Integer.parseInt(hex.substring(3, 5), 16);
		int b = Integer.parseInt(hex.substring(5, 7), 16);
		return ((0 & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
	}
}