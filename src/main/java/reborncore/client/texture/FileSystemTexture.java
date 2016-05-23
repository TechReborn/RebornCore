package reborncore.client.texture;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Mark on 25/03/2016.
 */
public class FileSystemTexture extends AbstractTexture
{
	private static final Logger logger = LogManager.getLogger();
	protected final File textureLocation;
	BufferedImage image;

	public FileSystemTexture(File textureResourceLocation)
	{
		this.textureLocation = textureResourceLocation;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException
	{
		this.deleteGlTexture();
		if(image == null){
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
						if(stream == null){
							try
							{
								stream = new FileInputStream(textureLocation);
							} catch (FileNotFoundException e)
							{
								e.printStackTrace();
							}
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
						if(stream != null){
							stream.close();
						}
					}
				};
				image= TextureUtil.readBufferedImage(iresource.getInputStream());
			} finally
			{
				IOUtils.closeQuietly(iresource);
			}
		}
		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false, false);
	}
}
