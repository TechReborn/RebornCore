/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by modmuss50 on 23/05/2016.
 */
public class InputStreamTexture extends AbstractTexture {
	private static final Logger logger = LogManager.getLogger();
	protected final InputStream textureLocation;
	BufferedImage image;
	String name;

	public InputStreamTexture(InputStream textureResourceLocation, String name) {
		this.textureLocation = textureResourceLocation;
		this.name = name;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		this.deleteGlTexture();
		if (image == null) {
			IResource iresource = null;
			try {
				iresource = new IResource() {

					@Override
					public ResourceLocation getResourceLocation() {
						return new ResourceLocation("reborncore:loaded/" + name);
					}

					@Override
					public InputStream getInputStream() {
						return textureLocation;
					}

					@Override
					public boolean hasMetadata() {
						return false;
					}

					@Override
					public <T extends IMetadataSection> T getMetadata(String sectionName) {
						return null;
					}

					@Override
					public String getResourcePackName() {
						return "reborncore";
					}

					@Override
					public void close() throws IOException {

					}
				};
				image = TextureUtil.readBufferedImage(iresource.getInputStream());
			} finally {
				IOUtils.closeQuietly(iresource);
			}
		}
		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false, false);
	}
}
