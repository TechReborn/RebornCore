package reborncore.client.guibuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import reborncore.client.RenderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class GuiBuilder
{
    static ResourceLocation resourceLocation;
    public static final ResourceLocation defaultTextureSheet = new ResourceLocation("reborncore", "textures/gui/guiElements.png");

    public GuiBuilder(ResourceLocation resourceLocation)
    {
        this.resourceLocation = resourceLocation;
    }

    public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
        gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
        gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
        gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
    }

    public void drawEnergyBar(GuiScreen gui, int x, int y, int height, int energyStored, int maxEnergyStored, int mouseX, int mouseY, String powerType)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

        gui.drawTexturedModalRect(x, y, 0, 150, 14, height);
        gui.drawTexturedModalRect(x, y + height - 1, 0, 255, 14, 1);
        int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
        gui.drawTexturedModalRect(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

        if (isInRect(x, y, 14, height, mouseX, mouseY))
        {
            List<String> list = new ArrayList<String>();
            list.add(energyStored + " / " + maxEnergyStored + " " + powerType);
            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRendererObj);
            GlStateManager.disableLighting();
        }
    }

    public void drawPlayerSlots(GuiScreen gui, int posX, int posY, boolean center)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

        if (center)
        {
            posX -= 81;
        }

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                gui.drawTexturedModalRect(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
            }
        }

        for (int x = 0; x < 9; x++)
        {
            gui.drawTexturedModalRect(posX + x * 18, posY + 58, 150, 0, 18, 18);
        }
    }

    public void drawSlot(GuiScreen gui, int posX, int posY)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        gui.drawTexturedModalRect(posX, posY, 150, 0, 18, 18);
    }

    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public void drawString(GuiScreen gui, String string, int x, int y)
    {
        gui.mc.fontRendererObj.drawString(string, x, y, 16777215);
    }

    public void drawProgressBar(GuiScreen gui, double progress, int x, int y)
    {
        gui.mc.getTextureManager().bindTexture(resourceLocation);
        gui.drawTexturedModalRect(x, y, 150, 18, 22, 15);
        int j = (int) (progress);
        if (j > 0)
        {
            gui.drawTexturedModalRect(x, y, 150, 34, j + 1, 15);
        }
    }

    public void drawTank(GuiScreen gui, FluidTank tank, int x, int y, float zLevel, int width, int height, int mouseX, int mouseY)
    {
        RenderUtil.renderGuiTank(tank, x, y, zLevel, width, height);
        if (isInRect(x, y, 14, height, mouseX, mouseY))
        {
            List<String> list = new ArrayList<String>();
            if(tank.getFluid() != null)
            {
                list.add(tank.getFluidAmount() + " / " + tank.getCapacity() + " " + tank.getFluid().getLocalizedName());
            }
            else
            {
                list.add("empty");
            }
            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRendererObj);
            GlStateManager.disableLighting();
        }
    }

    //TODO fix
    public void drawBurnBar(GuiScreen gui, double progress, int x, int y)
    {
        gui.mc.getTextureManager().bindTexture(resourceLocation);
        gui.drawTexturedModalRect(x, y, 150, 49, 22, 15);
        int j = (int) (progress);
        if (j > 0)
        {
            gui.drawTexturedModalRect(x, y, 150 , 64, 15, j + 1);

        }
    }

    public void drawOutputSlot(GuiScreen gui, int x, int y)
    {
        gui.mc.getTextureManager().bindTexture(resourceLocation);
        gui.drawTexturedModalRect(x, y, 174, 0, 26, 26);
    }
}
