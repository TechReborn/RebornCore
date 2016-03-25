package reborncore.shields.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Mark on 25/03/2016.
 */
public class FileSystemTexture extends AbstractTexture
{
	private static final Logger logger = LogManager.getLogger();
	protected final File textureLocation;

	public FileSystemTexture(File textureResourceLocation)
	{
		this.textureLocation = textureResourceLocation;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException
	{
		this.deleteGlTexture();
		IResource iresource = null;
		try
		{
			iresource = new IResource()
			{

				FileInputStream stream;

				@Override
				public ResourceLocation getResourceLocation()
				{
					return new ResourceLocation("reborncore:loaded/" + textureLocation.getName());
				}

				@Override
				public InputStream getInputStream()
				{
					try
					{
						stream = new FileInputStream(textureLocation);
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					return stream;
				}

				@Override
				public boolean hasMetadata()
				{
					return false;
				}

				@Override
				public <T extends IMetadataSection> T getMetadata(String sectionName)
				{
					return null;
				}

				@Override
				public String getResourcePackName()
				{
					return "reborncore";
				}

				@Override
				public void close() throws IOException
				{
					stream.close();
				}
			};
			BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());

			TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, false, false);
		} finally
		{
			IOUtils.closeQuietly(iresource);
		}
	}
}
