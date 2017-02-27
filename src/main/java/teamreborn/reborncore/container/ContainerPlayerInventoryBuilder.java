package teamreborn.reborncore.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.apache.commons.lang3.Range;
import teamreborn.reborncore.container.slot.FilteredSlot;
import teamreborn.reborncore.container.slot.ListenerSlot;

public final class ContainerPlayerInventoryBuilder
{

	private final InventoryPlayer  player;
	private final ContainerBuilder parent;
	private       Range<Integer>   main;
	private       Range<Integer>   hotbar;
	private       Range<Integer>   armor;

	ContainerPlayerInventoryBuilder(final ContainerBuilder parent, final InventoryPlayer player)
	{
		this.player = player;
		this.parent = parent;
	}

	/**
	 * Add the main inventory of the player, precisely the 3 x 9 array of slots.
	 *
	 * @param xStart
	 * @param yStart
	 * @return
	 */
	public ContainerPlayerInventoryBuilder inventory(final int xStart, final int yStart)
	{
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.parent.slots.add(new ListenerSlot(this.player, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
			}
		}
		this.main = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	/**
	 * Add the players hotbar.
	 *
	 * @param xStart
	 * @param yStart
	 * @return
	 */
	public ContainerPlayerInventoryBuilder hotbar(final int xStart, final int yStart)
	{
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 9; ++i)
		{
			this.parent.slots.add(new ListenerSlot(this.player, i, xStart + i * 18, yStart));
		}
		this.hotbar = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	/**
	 * Add the players main inventory with default coordinates (8, 94)
	 *
	 * @return
	 */
	public ContainerPlayerInventoryBuilder inventory()
	{
		return this.inventory(8, 94);
	}

	/**
	 * Add the players hotbar with default coordinates (8, 152)
	 *
	 * @return
	 */
	public ContainerPlayerInventoryBuilder hotbar()
	{
		return this.hotbar(8, 152);
	}

	/**
	 * Create a new sub builder that handle the players armor.
	 *
	 * @return a ContainerPlayerArmorInventoryBuilder that will return to this with ContainerPlayerArmorInventoryBuilder#addInventory
	 */
	public ContainerPlayerArmorInventoryBuilder armor()
	{
		return new ContainerPlayerArmorInventoryBuilder(this);
	}

	/**
	 * Close this builder and return the parent.
	 *
	 * @return
	 */
	public ContainerBuilder addInventory()
	{
		if (this.hotbar != null)
		{
			this.parent.addPlayerInventoryRange(this.hotbar);
		}
		if (this.main != null)
		{
			this.parent.addPlayerInventoryRange(this.main);
		}
		if (this.armor != null)
		{
			this.parent.addTileInventoryRange(this.armor);
		}

		return this.parent;
	}

	public static final class ContainerPlayerArmorInventoryBuilder
	{

		private final ContainerPlayerInventoryBuilder parent;
		private final int                             startIndex;

		ContainerPlayerArmorInventoryBuilder(final ContainerPlayerInventoryBuilder parent)
		{
			this.parent = parent;
			this.startIndex = parent.parent.slots.size();
		}

		private ContainerPlayerArmorInventoryBuilder armor(final int index, final int xStart, final int yStart, final EntityEquipmentSlot slotType)
		{

			this.parent.parent.slots.add(new FilteredSlot(this.parent.player, index, xStart, yStart).setFilter(stack -> stack.getItem().isValidArmor(stack, slotType, this.parent.player.player)));
			return this;
		}

		/**
		 * Add the helmet slot from the players armor.
		 *
		 * @param xStart
		 * @param yStart
		 * @return
		 */
		public ContainerPlayerArmorInventoryBuilder helmet(final int xStart, final int yStart)
		{
			return this.armor(this.parent.player.getSizeInventory() - 2, xStart, yStart, EntityEquipmentSlot.HEAD);
		}

		/**
		 * Add the chestplate slot from the players armor.
		 *
		 * @param xStart
		 * @param yStart
		 * @return
		 */
		public ContainerPlayerArmorInventoryBuilder chestplate(final int xStart, final int yStart)
		{
			return this.armor(this.parent.player.getSizeInventory() - 3, xStart, yStart, EntityEquipmentSlot.CHEST);
		}

		/**
		 * Add the leggings slot from the players armor.
		 *
		 * @param xStart
		 * @param yStart
		 * @return
		 */
		public ContainerPlayerArmorInventoryBuilder leggings(final int xStart, final int yStart)
		{
			return this.armor(this.parent.player.getSizeInventory() - 4, xStart, yStart, EntityEquipmentSlot.LEGS);
		}

		/**
		 * Add the boots slot from the players armor.
		 *
		 * @param xStart
		 * @param yStart
		 * @return
		 */
		public ContainerPlayerArmorInventoryBuilder boots(final int xStart, final int yStart)
		{
			return this.armor(this.parent.player.getSizeInventory() - 5, xStart, yStart, EntityEquipmentSlot.FEET);
		}

		/**
		 * Add the entire players armor with predefined spacing between each slot. (Vertically aligned 18 pixels between each slot top pixel)
		 *
		 * @param xStart
		 * @param yStart
		 * @return
		 */
		public ContainerPlayerArmorInventoryBuilder complete(final int xStart, final int yStart)
		{
			return this.helmet(xStart, yStart).chestplate(xStart, yStart + 18).leggings(xStart, yStart + 18 + 18).boots(xStart, yStart + 18 + 18 + 18);
		}

		/**
		 * Close this builder and return the parent one.
		 *
		 * @return
		 */
		public ContainerPlayerInventoryBuilder addArmor()
		{
			this.parent.armor = Range.between(this.startIndex, this.parent.parent.slots.size() - 1);
			return this.parent;
		}
	}
}
