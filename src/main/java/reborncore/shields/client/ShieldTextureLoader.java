package reborncore.shields.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import reborncore.RebornCore;
import reborncore.shields.json.ShieldJsonLoader;
import reborncore.shields.json.ShieldUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Mark on 25/03/2016.
 */
public class ShieldTextureLoader
{

	public static ShieldTextureLoader instance;
	public int step = 0;
	public int steps = 1;
	public boolean showBar = false;
	public ArrayList<File> validFiles = new ArrayList<>();
	boolean hasStarted = false;
	File mcDir;

	public ShieldTextureLoader(File mcDir)
	{
		this.mcDir = mcDir;
	}

	@SubscribeEvent
	public void render(GuiScreenEvent.DrawScreenEvent event)
	{
		if (showBar)
		{
			drawBar();
		}
		if (!hasStarted && ShieldJsonLoader.hasValidJsonFile)
		{
			hasStarted = true;
			startDownload();
		}

	}

	public void startDownload()
	{
		showBar = true;
		new Thread(() ->
		{
			try
			{
				for (ShieldUser user : ShieldJsonLoader.shieldJsonFile.userList)
				{
					TextureDownloadUtils utils = new TextureDownloadUtils(this);
					File file = new File(mcDir, "reborncore/shieldTextures/" + user.username + ".png");
					utils.downloadFile("http://modmuss50.me/reborncore/textures/" + user.username + ".png",
							new File(mcDir, "reborncore/shieldTextures"), user.username + ".png");

					if (!file.exists() || !user.textureMd5.equals(ShieldJsonLoader.getMD5(file)))
					{
						RebornCore.logHelper.info(user.username + " texture failed to download");
					} else
					{
						validFiles.add(file);
					}
				}

			} catch (IOException e)
			{
				e.printStackTrace();
				showBar = false;
			}
		}).start();
		RebornCore.logHelper.info("Downlaoded " + validFiles.size() + " textures");
	}

	private void drawBar()
	{
		int barWidth = 110;
		int barHeight = 9;
		glPushMatrix();

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		// glTranslatef(320 - (float)barWidth / 2, 310, 0);
		GL11.glColor3f(1F, 1F, 1F);
		glScalef(1, 1, 1);
		glEnable(GL_TEXTURE_2D);
		glScalef(0.6F, 0.6F, 0.6F);
		fontRenderer.drawString("Reborncore" + " - " + "Downloading textures", 0, 0, 0x000000);
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();

		glPushMatrix();
		glTranslatef(0, 5, 0);
		GL11.glColor3f(0F, 0.0F, 0F);
		drawBox(barWidth, barHeight);

		GL11.glColor3f(0.5F, 0.5F, 0.5F);
		glTranslatef(1, 1, 0);
		drawBox(barWidth - 2, barHeight - 2);

		GL11.glColor3f(0.5F, 1F, 0.5F);
		drawBox((barWidth - 2) * (step + 1) / (steps + 1), barHeight - 2); // Step
																			// can
																			// sometimes
																			// be
																			// 0.

		glScalef(0.4F, 0.4F, 0.4F);
		glTranslated(95, 0, 0);
		String progress = percentage(steps, step) + "%";
		glTranslatef(((float) barWidth - 2) / 2 - fontRenderer.getStringWidth(progress), 2, 0);

		GL11.glColor3f(1F, 1F, 1F);
		glScalef(2, 2, 1);
		glEnable(GL_TEXTURE_2D);
		fontRenderer.drawString(progress, 0, 0, 0x000000);
		glPopMatrix();
	}

	public int percentage(int MaxValue, int CurrentValue)
	{
		if (CurrentValue == 0)
			return 0;
		return (int) ((CurrentValue * 100.0f) / MaxValue);
	}

	private void drawBox(int w, int h)
	{
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(0, h);
		glVertex2f(w, h);
		glVertex2f(w, 0);
		glEnd();
	}

}
