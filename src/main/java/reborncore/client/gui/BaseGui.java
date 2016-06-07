package reborncore.client.gui;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import reborncore.client.gui.componets.BaseTextures;

public class BaseGui extends GuiContainer
{
	Container container;

	TileEntity tileEntity;

	ResourceLocation overlays;

	ResourceLocation guitexture;

    EntityPlayer player;

    String name;

	public BaseGui(Container container, TileEntity tileEntity, EntityPlayer player, ResourceLocation overlays, ResourceLocation guitexture, String name)
	{
		super(container);
		this.container = container;
		this.tileEntity = tileEntity;
		this.overlays = overlays;
		this.guitexture = guitexture;
        this.name = name;
        this.player = player;
//		componentList = new ArrayList<IGuiComponent>();
//		registerComponets();
//		baseTextures = new BaseTextures();
	}

    @Override
    public void initGui()
    {
        super.initGui();
    }

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        String name = I18n.translateToLocal("tile." + this.name + ".name");
        this.fontRendererObj.drawString(name, this.xSize / 2 - 10 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        this.mc.getTextureManager().bindTexture(guitexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

//	@Override
//	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
//	{
//		this.mc.renderEngine.bindTexture(baseTexture);
//		int x = (this.width - this.xSize) / 2;
//		int y = (this.height - this.ySize) / 2;
//
//		baseTextures.background.draw(0, 0, this);
//
//		for (Object slotObj : container.inventorySlots)
//		{
//			if (slotObj instanceof Slot)
//			{
//				Slot slot = (Slot) slotObj;
//				baseTextures.slot.draw(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, this);
//			}
//		}
//
//		for (IGuiComponent component : componentList)
//		{
//			component.drawGuiContainerBackgroundLayer(this);
//		}
//	}
//
//	public Container getContainer()
//	{
//		return container;
//	}
//
//	public TileEntity getTileEntity()
//	{
//		return tileEntity;
//	}
//
//	/**
//	 * Override this and add components in here.
//	 */
//	public void registerComponets()
//	{
//
//	}
//
//	public void registerComponet(IGuiComponent component)
//	{
//		componentList.add(component);
//	}
//
//	@Override
//	public void initGui()
//	{
//		super.initGui();
//		for (IGuiComponent component : componentList)
//		{
//			component.initGui(this);
//		}
//	}
//
//	@Override
//	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
//	{
//		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
//		for (IGuiComponent component : componentList)
//		{
//			component.drawScreen(this);
//		}
//	}
//
//	@Override
//	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
//	{
//		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
//		for (IGuiComponent component : componentList)
//		{
//			component.drawGuiContainerBackgroundLayer(this);
//		}
//	}
//
//	@Override
//	public void onGuiClosed()
//	{
//		super.onGuiClosed();
//		for (IGuiComponent component : componentList)
//		{
//			component.onGuiClosed(this);
//		}
//	}
//
//	@Override
//	public void updateScreen()
//	{
//		super.updateScreen();
//		for (IGuiComponent component : componentList)
//		{
//			component.updateScreen(this);
//		}
//	}
//
//	public BaseTextures getBaseTextures()
//	{
//		return baseTextures;
//	}
//
//	public int getXSize()
//	{
//		return xSize;
//	}
//
//	public int getYSize()
//	{
//		return ySize;
//	}

}
