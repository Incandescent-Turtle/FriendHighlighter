package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * A general utility class for this mod. It helps with the creation of Text, colors, and more.
 */
public class FHUtils
{
	private FHUtils(){}

	/**
	 *	bolds and colors text accordingly
	 * @param msg the message to be bolded and coloured
	 * @param color the color the message will be in rgb form
	 * @return a {@link MutableText} that is bolded and colored accordingly
	 * @see FHUtils#getBoldAndColored(String, Formatting)
	 */
	public static MutableText getBoldAndColored(String msg, int color)
	{
		return Text.literal(msg).styled(style -> style.withBold(true).withColor(color));
	}

	/**
	 *	bolds and colors text accordingly
	 * @param msg the message to be bolded and coloured
	 * @param color the color the message will be
	 * @return a {@link MutableText} that is bolded and colored accordingly
	 * @see FHUtils#getBoldAndColored(String, int)
	 */
	public static MutableText getBoldAndColored(String msg, Formatting color)
	{
		return getBoldAndColored(msg, color.getColorValue());
	}

	/**
	 * returns the input text colored
	 * @param str the string to be colored
	 * @param rgb the color as an rgb int to color with
	 * @return the string as {@link MutableText}, colored
	 */
	public static MutableText colorText(String str, int rgb)
	{
		return Text.literal(str).styled(style -> style.withColor(rgb));
	}

	/**
	 * returns the input text colored
	 * @param str the string to be colored
	 * @param color the color to use
	 * @return the string as {@link MutableText}, colored
	 */
	public static MutableText colorText(String str, Formatting color)
	{
		return colorText(str, color.getColorValue());
	}

	/**
	 * bolds the message and colors it green
	 * @param msg the message to be returned
	 * @return returns the message bolded and green
	 */
	public static MutableText getPositiveMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.GREEN);
	}

	/**
	 * bolds the message and colors it red
	 * @param msg the message to be returned
	 * @return returns the message bolded and red
	 */
	public static MutableText getNegativeMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.RED);
	}

	/**
	 * a method to convert a hex string to an rgb int
	 * @param hex the hex string starting with # and 1-6 hex digits
	 * @return the hex as an rgb int, filling in 0 for any missing hex digits
	 */
	public static int hexToRGB(String hex)
	{
		//	filling in 0 for any missing hex digits
		hex += "0".repeat(7 - hex.length());
		hex = hex.toUpperCase();
		//	taking the first two hex digits, and parsing them as base 16
		int r = Integer.parseInt(hex.substring(1, 3), 16);
		int g = Integer.parseInt(hex.substring(3, 5), 16);
		int b = Integer.parseInt(hex.substring(5, 7), 16);
		//	shifting the bits to form the rgb int
		return ((0 & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
	}

	/**
	 *
	 * @param rgb
	 * @return
	 */
	public static String rgbToHex(int rgb)
	{
		return String.format("#%06x", rgb & 0xFFFFFF);
	}

	public static MutableText createCopyableText(Text input, boolean withQuotes)
	{
		MutableText txt = Text.literal("");
		txt.append(input);
		txt.styled(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, withQuotes ? "\""+txt.getString()+"\"" : txt.getString()))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy!"))));
		return txt;
	}
}