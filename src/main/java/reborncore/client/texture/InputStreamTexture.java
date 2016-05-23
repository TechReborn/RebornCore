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
import java.io.*;

/**
 * Created by modmuss50 on 23/05/2016.
 */
public class InputStreamTexture extends AbstractTexture {
    private static final Logger logger = LogManager.getLogger();
    protected final InputStream textureLocation;
    BufferedImage image;
    String name;

    public InputStreamTexture(InputStream textureResourceLocation, String name)
    {
        this.textureLocation = textureResourceLocation;
        this.name = name;
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

                    @Override
                    public ResourceLocation getResourceLocation()
                    {
                        return new ResourceLocation("reborncore:loaded/" + name);
                    }

                    @Override
                    public InputStream getInputStream()
                    {
                        return textureLocation;
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
