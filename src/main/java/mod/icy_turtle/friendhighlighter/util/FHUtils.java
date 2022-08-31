package mod.icy_turtle.friendhighlighter.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A general utility class for this mod. It helps with the creation of Text, colors, and more.
 */
public class FHUtils
{
	private FHUtils(){}

	/**
	 * Bolds and colors text accordingly.
	 * @param msg the message to be bolded and coloured.
	 * @param color the color the message will be in rgb form.
	 * @return a {@link MutableText} that is bolded and colored accordingly.
	 * @see #getBoldAndColored(String, Formatting)
	 */
	public static MutableText getBoldAndColored(String msg, int color)
	{
		return Text.literal(msg).styled(style -> style.withBold(true).withColor(color));
	}

	/**
	 * Bolds and colors text accordingly.
	 * @param msg the message to be bolded and coloured.
	 * @param color the color the message will be.
	 * @return a {@link MutableText} that is bolded and colored accordingly.
	 * @see #getBoldAndColored(String, int)
	 */
	public static MutableText getBoldAndColored(String msg, @NotNull Formatting color)
	{
		return getBoldAndColored(msg, color.getColorValue());
	}

	/**
	 * Returns the input text colored.
	 * @param str the string to be colored.
	 * @param rgb the color as an rgb int to color with.
	 * @return the string as {@link MutableText}, colored.
	 * @see #colorText(String, Formatting)
	 */
	public static MutableText colorText(String str, int rgb)
	{
		return Text.literal(str).styled(style -> style.withColor(rgb));
	}

	/**
	 * Returns the input text colored.
	 * @param str the string to be colored.
	 * @param color the color to use.
	 * @return the string as {@link MutableText}, colored.
	 * @see #colorText(String, int)
	 */
	public static MutableText colorText(String str, @NotNull Formatting color)
	{
		return colorText(str, color.getColorValue());
	}

	/**
	 * Bolds the message and colors it green.
	 * @param msg the message to be returned.
	 * @return returns the message bolded and green.
	 * @see #getMessageWithConnotation(String, String, boolean)
	 * @see #getBoldAndColored(String, Formatting)
	 * @see #getBoldAndColored(String, int)
	 */
	public static MutableText getPositiveMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.GREEN);
	}

	/**
	 * Bolds the message and colors it red.
	 * @param msg the message to be returned.
	 * @return returns the message bolded and red.
	 * @see #getMessageWithConnotation(String, String, boolean)
	 * @see #getBoldAndColored(String, Formatting)
	 * @see #getBoldAndColored(String, int)
	 */
	public static MutableText getNegativeMessage(String msg)
	{
		return FHUtils.getBoldAndColored(msg, Formatting.RED);
	}

	/**
	 * Returns either the positive text or negative text depending on the boolean.
	 * @param positiveMessage the message to display when the boolean is true.
	 * @param negativeMessage the message to display when the boolean is false.
	 * @param positiveConnotation whether the positive or negative message should be used.
	 * @return the positive or negative message with {@link #getPositiveMessage(String)} or {@link #getNegativeMessage(String)} applied respectively.
	 * @see #getPositiveMessage(String)
	 * @see #getNegativeMessage(String)
	 */
	public static MutableText getMessageWithConnotation(String positiveMessage, String negativeMessage, boolean positiveConnotation)
	{
		if(positiveConnotation)
		{
			return FHUtils.getPositiveMessage(positiveMessage);
		} else {
			return FHUtils.getNegativeMessage(negativeMessage);
		}
	}

	/**
	 * Converts a hex string to an rgb int.
	 * @param hex the hex string starting with # and 1-6 hex digits.
	 * @return the hex as an rgb int, filling in 0 for any missing hex digits.
	 * @see #rgbToHex(int) 
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
		return (0) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF));
	}

	/**
	 * Converts an rgb int into a hex string starting with '<b>#</b>'.
	 * @param rgb the rgb int.
	 * @return the hex code for the color, starting with '<b>#</b>'
	 * @see #hexToRGB(String) 
	 */
	public static String rgbToHex(int rgb)
	{
		return String.format("#%06x", rgb & 0xFFFFFF);
	}

	/**
	 * Adds a hover and click event to a {@link Text} object to allow for its content to be copied
	 * @param input the text to be displayed and made copyable.
	 * @param withQuotes whether the input should have quotes appended when copying.
	 * @return a new text object with a click and hover event for copying.
	 */
	public static MutableText createCopyableText(Text input, boolean withQuotes)
	{
		MutableText txt = Text.literal("");
		txt.append(input);
		txt.styled(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, withQuotes ? "\""+txt.getString()+"\"" : txt.getString()))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy!"))));
		return txt;
	}

	/**
	 * Encloses all strings in the list with "", returns a new list and doesn't mutate the old one.
	 * @param list the list of strings to enclose with "", will not be mutated.
	 * @return a new list with all strings enclosed in quotes.
	 */
	public static List<String> surroundListItemsWithQuotes(List<String> list)
	{
		return list.stream().map(name -> "\""+name+"\"").collect(Collectors.toList());
	}

	/**
	 * Testing if the input is a '#' followed by 1-6 hex digits
	 * @param input the string input to test
	 * @return whether the input is a valid hex code
	 */
	public static boolean isValidHexCode(String input)
	{
		return input.matches("^#([0-9A-Fa-f]{1,6})$");
	}
}