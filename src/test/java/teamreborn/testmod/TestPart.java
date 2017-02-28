package teamreborn.testmod;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import teamreborn.reborncore.api.multipart.IPartContainer;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.impl.PartRegistry;
import teamreborn.reborncore.multipart.BasePart;

import java.util.Collections;
import java.util.List;

@RebornRegistry (modID = "testmod")
@PartRegistry
public class TestPart extends BasePart
{

	@Override
	public String getIdenteifyer()
	{
		return "testpart";
	}

	@Override
	public ModelResourceLocation getModel(IPartContainer container)
	{
		return new ModelResourceLocation("testmod", getIdenteifyer());
	}

	@Override
	public List<AxisAlignedBB> getCollisonBoxes(IPartContainer container, Entity entity)
	{
		return Collections.singletonList(new AxisAlignedBB(2, 2, 2, 8, 8, 8));
	}

	@Override
	public List<ItemStack> getDrops(IPartContainer container)
	{
		return Collections.singletonList(new ItemStack(Items.APPLE));
	}

	@Override
	public void update(IPartContainer container)
	{
		super.update(container);
		container.getWorld().setBlockState(container.getPos().up(), Blocks.DIAMOND_BLOCK.getDefaultState());
	}
}
