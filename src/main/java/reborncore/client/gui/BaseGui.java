package reborncore.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class BaseGui extends GuiContainer {
	Container container;
	TileEntity tileEntity;
	ResourceLocation overlays;
	ResourceLocation guitexture;
	EntityPlayer player;
	String name;

	public BaseGui(Container container, TileEntity tileEntity, EntityPlayer player, ResourceLocation overlays, ResourceLocation guitexture, String name) {
		super(container);
		this.container = container;
		this.tileEntity = tileEntity;
		this.overlays = overlays;
		this.guitexture = guitexture;
		this.name = name;
		this.player = player;
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		String name = I18n.translateToLocal("tile." + this.name + ".name");
		this.fontRenderer.drawString(name, this.xSize / 2 - 6 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		this.mc.getTextureManager().bindTexture(guitexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
