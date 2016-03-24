package ic2.api.event.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RecipeEvent extends Event
{
	final ItemStack input;
	final ItemStack output;

	public RecipeEvent(final ItemStack input, final ItemStack output)
	{
		this.input = input;
		this.output = output;
	}

	public final ItemStack getInput()
	{
		return this.input.copy();
	}

	public final ItemStack getOutput()
	{
		return this.output.copy();
	}
}
