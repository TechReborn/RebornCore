package reborncore.common.fluid;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.class_4538;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

import java.util.function.Supplier;

public abstract class RebornFluid extends BaseFluid {

	private final boolean still;

	private final FluidSettings fluidSettings;
	private final Supplier<RebornFluidBlock> fluidBlockSupplier;
	private final Supplier<RebornBucketItem> bucketItemSuppler;
	private final Supplier<RebornFluid> flowingSuppler;
	private final Supplier<RebornFluid> stillSuppler;

	public RebornFluid(boolean still, FluidSettings fluidSettings, Supplier<RebornFluidBlock> fluidBlockSupplier, Supplier<RebornBucketItem> bucketItemSuppler, Supplier<RebornFluid> flowingSuppler, Supplier<RebornFluid> stillSuppler) {
		this.still = still;
		this.fluidSettings = fluidSettings;
		this.fluidBlockSupplier = fluidBlockSupplier;
		this.bucketItemSuppler = bucketItemSuppler;
		this.flowingSuppler = flowingSuppler;
		this.stillSuppler = stillSuppler;
		BlockRenderLayerMap.INSTANCE.putFluid(this, BlockRenderLayer.field_9179);
	}

	public FluidSettings getFluidSettings() {
		return fluidSettings;
	}

	@Override
	public RebornFluid getFlowing() {
		return flowingSuppler.get();
	}

	@Override
	public RebornFluid getStill() {
		return stillSuppler.get();
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	public boolean isStill(FluidState fluidState) {
		return still;
	}

	@Override
	protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {

	}

	@Override
	protected int method_15733(class_4538 world) {
		return 4;
	}

	@Override
	protected int getLevelDecreasePerBlock(class_4538 world) {
		return 1;
	}

	@Override
	public Item getBucketItem() {
		return bucketItemSuppler.get();
	}

	@Override
	protected boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		//TODO wat is this?
		return false;
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return getFlowing() == fluid || getStill() == fluid;
	}

	@Override
	public int getTickRate(class_4538 world) {
		return 10;
	}

	@Override
	protected float getBlastResistance() {
		return 100F;
	}

	@Override
	protected BlockState toBlockState(FluidState fluidState) {
		return fluidBlockSupplier.get().getDefaultState().with(FluidBlock.LEVEL, method_15741(fluidState));
	}

	@Override
	public int getLevel(FluidState fluidState) {
		return still ? 8 : fluidState.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Fluid, FluidState> stateBuilder) {
		super.appendProperties(stateBuilder);
		if(!still){
			stateBuilder.add(LEVEL);
		}
	}

}
