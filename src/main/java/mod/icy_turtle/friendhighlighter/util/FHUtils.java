package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class FHUtils
{
	private FHUtils(){}

	public static MutableText getBoldAndColored(String msg, int color)
	{
		return Text.literal(msg).styled(style -> style.withBold(true).withColor(color));
	}

	public static MutableText getBoldAndColored(String msg, Formatting color)
	{
		return getBoldAndColored(msg, color.getColorValue());
	}

	public static MutableText colorText(String str, int rgb)
	{
		return Text.literal(str).styled(style -> style.withColor(rgb));
	}

	public static MutableText colorText(String str, Formatting color)
	{
		return colorText(str, color.getColorValue());
	}

	public static MutableText getPositiveMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.GREEN);
	}

	public static MutableText getNegativeMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.RED);
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

	public static MutableText createCopyableText(Text input)
	{
		MutableText txt = Text.literal("");
		txt.append(input);
		txt.styled(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, txt.getString()))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy!"))));
		return txt;
	}
}