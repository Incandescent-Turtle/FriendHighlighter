package mod.icy_turtle.friendhighlighter.command.commands.list;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import mod.icy_turtle.friendhighlighter.command.CommandUtils;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BiFunction;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AdvancedListCommand extends Command
{
	public AdvancedListCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("advanced")
				.executes(context -> cmdHandler.advancedListChatMsg.sendInChat());
	}

	public static MutableText createAdvancedList()
	{
		var map = FriendsListHandler.getFriendsMap();
		var title = Text.literal("\nFriends List").styled(style -> style.withUnderline(true).withBold(true));

		var friends = Text.literal("");
		for(var friend : map.values())
		{
			friends.append(createDeleteButton(friend))
					.append(" ")
					.append(ListCommand.createToggleableName(friend))
					.append("\n")
					.append(createFriendBooleans(friend))
					.append("\n");
		}

		var txt = Text.literal("");
		txt.append(title)
				.append(" | ")
				.append(createToggleButton());

		if(map.isEmpty())
		{
			txt.append(" - Empty");
		} else {
			txt.append("\n\n")
					.append(friends)
					.append("______\n")
					.append(createClearButton())
					.append("\n");
		}
		return txt;
	}

	/**
	 * Creates a clickable {@link Text} that toggles the friend highlighter.
	 * @return the toggle button.
	 */
	private static Text createToggleButton()
	{
		return CommandUtils.addHoverAndClickEvent(
				FHUtils.getMessageWithConnotation("Enabled", "Disabled", FriendHighlighter.isHighlighterEnabled),
				"Click to toggle Friend Highlighter",
				"/fh toggle"
		);
	}

	/**
	 * Creates a character that deletes the given friend when clicked.
	 * @param friend the friend to delete when clicked.
	 * @return the delete button.
	 */
	private static Text createDeleteButton(HighlightedFriend friend)
	{
		return CommandUtils.addHoverAndClickEvent(
				FHUtils.colorText("✖", Formatting.RED.getColorValue()),
				"Remove " + friend.name + " from friends list",
				"/fh remove \"" + friend.name + "\""
		);
	}

	/**
	 * Creates the text that displays the values of the boolean values of a {@link HighlightedFriend} with toggle capabilities.
	 * @param friend the friend of which the boolean refer.
	 * @return the booleans to display
	 */
	private static Text createFriendBooleans(HighlightedFriend friend)
	{
		//  click event to send a chat message to toggle the booleans
		BiFunction<Boolean, Boolean, String> clickEvent = (onlyPlayers, outlineFriend) ->  "/fh add " + String.join(" ", "\"" + friend.name + "\"", FHUtils.rgbToHex(friend.color), ""+onlyPlayers, ""+outlineFriend);

		FHUtils.colorText(friend.onlyPlayers ? "Only Players" : "All Entities", friend.onlyPlayers ? 0xA7C7E7 : 0xFF5F1F);
		MutableText onlyPlayers = CommandUtils.addHoverAndClickEvent(
				friend.onlyPlayers ? FHUtils.colorText("Only Player", 0xA7C7E7) : FHUtils.colorText("All entities", 0xFF5F1F),
				"Click to change to " + (!friend.onlyPlayers ? "only players" : "all entities"),
				clickEvent.apply(!friend.onlyPlayers, friend.justNameTag));


		MutableText outlineFriend = CommandUtils.addHoverAndClickEvent(
				friend.justNameTag ? FHUtils.colorText("Nametag Only", 0xFFFFFF) : FHUtils.colorText("Nametag & Outline", 0x008800),
				"Click to change to " + (friend.justNameTag ? "nametag & outline" : "nametag only"),
				clickEvent.apply(friend.onlyPlayers, !friend.justNameTag));

		return Text.literal(" ↳ ").append(onlyPlayers).append(" | ").append(outlineFriend);
	}

	/**
	 * Creates a clickable {@link Text} that suggests the clear command
	 * @return the clear button.
	 */
	private static Text createClearButton()
	{
		return CommandUtils.addHoverAndClickEvent(
				FHUtils.getNegativeMessage("Clear"),
				Text.literal("Clear Friends List"),
				"/fh clear"
		);
	}
}
