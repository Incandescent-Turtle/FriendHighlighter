package mod.icy_turtle.friendhighlighter.util;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import net.minecraft.text.Text;

import java.util.List;

public class MultiPartGUIElement<T> extends MultiElementListEntry<T>
{
	public MultiPartGUIElement(Text categoryName, T object, List<AbstractConfigListEntry<?>> list, boolean defaultExpanded)
	{
		super(categoryName, object, list, defaultExpanded);
	}

	@Override
	public Text getDisplayedFieldName() {
		return getFieldName().copy();
	}
}
