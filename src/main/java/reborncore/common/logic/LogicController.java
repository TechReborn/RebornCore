package reborncore.common.logic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import reborncore.RebornCore;
import reborncore.RebornRegistry;
import reborncore.client.guibuilder.GuiBuilder;
import reborncore.common.network.VanillaPacketDispatcher;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public abstract class LogicController extends TileEntity
{
    //Inv
    String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ItemStackHandler inv = new ItemStackHandler(getInvSize());

    public abstract int getInvSize();

    public boolean hasInv()
    {
        if (getInvSize() != 0)
        {
            return true;
        }
        return false;
    }

    public ItemStackHandler getInv()
    {
        return this.inv;
    }

    public GuiBuilder builder = new GuiBuilder(GuiBuilder.defaultTextureSheet);

    @SideOnly(Side.CLIENT)
    public GuiBuilder getBuilder()
    {
        return builder;
    }

    public int getXSize()
    {
        return 176;
    }

    public int getYsize()
    {
        return 176;
    }

    @SideOnly(Side.CLIENT)
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, LogicGui gui)
    {
        getBuilder().drawDefaultBackground(gui, guiLeft, guiTop, xSize, ySize);
        getBuilder().drawPlayerSlots(gui, guiLeft + xSize / 2, guiTop + 84, true);
        if (getSlots() != null)
        {
            for (Slot s : getSlots())
            {
                getBuilder().drawSlot(gui, guiLeft + s.xPos - 1, guiTop + s.yPos - 1);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY, GuiContainer gui, int guiLeft, int guiTop) {}

    @SideOnly(Side.CLIENT)
    public List<GuiButton> getButtons()
    {
        return null;
    }

    //Container
    public abstract List<Slot> getSlots();

    public int inventoryOffsetX()
    {
        return 8;
    }

    public int inventoryOffsetY()
    {
        return 85;
    }

    //Block
    public TileEntity createNewTileEntity(World world, int meta){
	    try {
		    return this.getClass().newInstance();
	    } catch (InstantiationException | IllegalAccessException e) {
		    e.printStackTrace();
	    }
	    return null;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
	    openGui(playerIn, (LogicController) worldIn.getTileEntity(pos));
	    return true;
    }

    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    public static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {}

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced){}

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {}

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {}

    public void actionPerformed(int buttonID){}

    //NBT
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        if(hasInv())
        {
            compound.merge(getInv().serializeNBT());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(hasInv())
        {
            inv.deserializeNBT(compound);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(hasInv() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(hasInv() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInv());
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
    }

    public void sync()
    {
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
    }

    public void initBlock(LogicBlock block){
    }

    public static void openGui(EntityPlayer player, LogicController machine)
    {
        if (!player.isSneaking())
        {
            player.openGui(RebornCore.INSTANCE, 0, machine.world, machine.pos.getX(), machine.pos.getY(), machine.pos.getZ());
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(Block block) {
        RebornRegistry.registerItemModel(block, 0);
    }
}
