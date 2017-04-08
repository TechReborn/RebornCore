package reborncore.common.advanced;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
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
import reborncore.client.guibuilder.GuiBuilder;
import reborncore.common.network.VanillaPacketDispatcher;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public abstract class AdvancedTileEntity extends TileEntity
{
    //Inv
    public abstract String getName();

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
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, AdvancedGui gui)
    {
        getBuilder().drawDefaultBackground(gui, guiLeft, guiTop, xSize, ySize);
        getBuilder().drawPlayerSlots(gui, guiLeft + xSize / 2, guiTop + 93, true);
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

    //Container
    public abstract List<Slot> getSlots();

    public int inventoryOffsetX()
    {
        return 8;
    }

    public int inventoryOffsetY()
    {
        return 94;
    }

    //Block
    public abstract TileEntity createNewTileEntity(World world, int meta);

    public abstract boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ);

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
}
