package mod.icy_turtle.friendhighlighter.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import mod.icy_turtle.friendhighlighter.config.FriendsListHandler;
import mod.icy_turtle.friendhighlighter.config.HighlightedFriend;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import mod.icy_turtle.friendhighlighter.util.MultiPartGUIElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FriendsListPage
{
	public static AbstractConfigListEntry createFriendsList(ConfigEntryBuilder entryBuilder)
	{
		return new NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>>(
				Text.literal("Friend's List"),
				mapToFriendsList(FriendsListHandler.getFriendsMap()), // initial
				true, // defauilt expand the whole list (not the children though)
				Optional::empty, //  tool tip
				list -> FriendsListHandler.setFriendsMap(friendListToMap(list)),
				() -> mapToFriendsList(FriendsListHandler.getFriendsMap()),
				entryBuilder.getResetButtonKey(),
				true,
				true, // so it is inserted at the top
				(friendIn, nestedListListEntry) -> createNewCell(entryBuilder, friendIn, nestedListListEntry)
		);
	}

	private static MultiPartGUIElement createNewCell(ConfigEntryBuilder entryBuilder, @Nullable HighlightedFriend friendIn, NestedListListEntry<HighlightedFriend, MultiElementListEntry<HighlightedFriend>> nestedListListEntry)
	{
		final var friend = friendIn == null ? new HighlightedFriend() : friendIn;
		return new MultiPartGUIElement<>(
				getFriendHeading(friend),
				friend,
				Arrays.asList(
						createNameField(entryBuilder, friend),
						createColorField(entryBuilder, friend),
						createOnlyPlayersToggle(entryBuilder, friend),
						createJustNameToggle(entryBuilder, friend),
						createEnabledToggle(entryBuilder, friend)),
				friend.getName().equals(""));
	}

	private static Text getFriendHeading(HighlightedFriend friend)
	{
		var coloredName = FHUtils.colorText(friend.getName(), friend.getColor());
		var enabledSymbol = FHUtils.getMessageWithConnotation(GuiUtil.INDICATOR_SQUARE, friend.isEnabled());

		var heading = Text.literal("");
		heading.append(coloredName);
		heading.append(Text.literal(" "));
		heading.append(enabledSymbol);
		return heading;
	}

	private static StringListEntry createNameField(ConfigEntryBuilder entryBuilder, HighlightedFriend friend)
	{
		return entryBuilder.startTextField(Text.literal("Name"), friend.getName())
				.setSaveConsumer(str -> friend.setName(str))
				.setErrorSupplier(str -> str.equals("") ? Optional.of(Text.of("Friend Name cannot be blank.")) : Optional.empty())
				.build();
	}

	private static ColorEntry createColorField(ConfigEntryBuilder entryBuilder, HighlightedFriend friend)
	{
		return entryBuilder.startColorField(Text.literal("Color"), friend.getColor())
				.setSaveConsumer(color -> friend.setColor(color))
				.build();
	}

	private static BooleanListEntry createOnlyPlayersToggle(ConfigEntryBuilder entryBuilder, HighlightedFriend friend)
	{
		return entryBuilder.startBooleanToggle(Text.literal("Only Players"), friend.isOnlyPlayers())
				.setSaveConsumer(onlyPlayers -> friend.setOnlyPlayers(onlyPlayers))
				.setTooltipSupplier(GuiUtil.createToolTip("Whether only player's with this name will get highlighted."))
				.build();
	}

	private static BooleanListEntry createJustNameToggle(ConfigEntryBuilder entryBuilder, HighlightedFriend friend)
	{
		return entryBuilder.startBooleanToggle(Text.literal("Just Nametag"), friend.isJustNameTag())
				.setSaveConsumer(justNametag -> friend.setJustNameTag(justNametag))
				.setTooltipSupplier(GuiUtil.createToolTip("Whether " + (friend.isOnlyPlayers() ? "players" : "entities") + " with this name will be outlined in addition their name tag always showing and being colored."))
				.build();
	}

	private static BooleanListEntry createEnabledToggle(ConfigEntryBuilder entryBuilder, HighlightedFriend friend)
	{
		return entryBuilder.startBooleanToggle(Text.literal("Enabled"), friend.isEnabled())
				.setSaveConsumer(friend::setEnabled)
				.setTooltipSupplier(GuiUtil.createToolTip("Toggles whether this friend will currently be highlighted and have its name colored."))
				.build();
	}

	/**
	 * Converts the given map to a list of friends.
	 * @param map a map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
	 * @return the map,
	 */
	private static List<HighlightedFriend> mapToFriendsList(LinkedHashMap<String, HighlightedFriend> map)
	{
		var list = new ArrayList<>(map.values());
		//  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
		Collections.reverse(list);
		return list;
	}

	/**
	 * Converts the given list of {@link HighlightedFriend}s to a map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
	 * @param list the friends list to convert.
	 * @return the map with the friend's name as the key, and the {@link HighlightedFriend} object as the value.
	 */
	private static LinkedHashMap<String, HighlightedFriend> friendListToMap(List<HighlightedFriend> list)
	{
		LinkedHashMap<String, HighlightedFriend> map = new LinkedHashMap<>();
		//  reverses as the list needs to have the newest entries at the top because of ClothConfig, and the map needs newest at the bottom because of the list command.
		Collections.reverse(list);
		list.forEach(f -> map.put(f.getName(), f));
		return map;
	}
}
