package reborncore.mcmultipart.microblock;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import reborncore.mcmultipart.multipart.IMultipart;
import reborncore.mcmultipart.raytrace.PartMOP;

public class MicroblockDelegate {

	protected final IMicroblock delegated;

	public MicroblockDelegate(IMicroblock delegated) {

		this.delegated = delegated;
	}

	public boolean harvest(EntityPlayer player, PartMOP hit) {

		return false;
	}

	public Optional<Float> getStrength(EntityPlayer player, PartMOP hit) {

		return Optional.absent();
	}

	public void onPartChanged(IMultipart part) {

	}

	public void onNeighborBlockChange(Block block) {

	}

	public void onNeighborTileChange(EnumFacing facing) {

	}

	public void onAdded() {

	}

	public void onRemoved() {

	}

	public void onLoaded() {

	}

	public void onUnloaded() {

	}

	public Optional<Boolean> onActivated(EntityPlayer player, EnumHand hand, PartMOP hit) {

		return Optional.absent();
	}

	public boolean onClicked(EntityPlayer player, PartMOP hit) {

		return false;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {

	}

	public void writeUpdatePacket(PacketBuffer buf) {

	}

	public void readUpdatePacket(PacketBuffer buf) {

	}

	public final void sendUpdatePacket() {

		delegated.sendUpdatePacket();
	}

	public Optional<Boolean> canConnectRedstone(EnumFacing side) {

		return Optional.absent();
	}

	public Optional<Integer> getWeakSignal(EnumFacing side) {

		return Optional.absent();
	}

	public Optional<Integer> getStrongSignal(EnumFacing side) {

		return Optional.absent();
	}
}
