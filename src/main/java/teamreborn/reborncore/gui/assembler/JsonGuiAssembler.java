package teamreborn.reborncore.gui.assembler;

import teamreborn.reborncore.RebornCore;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Created by Prospector.
 */
public class JsonGuiAssembler extends GuiAssembler {

	public static File json;
	public static List<RectangleElement> defRects = new ArrayList<>();
	public static List<GradientElement> defRectGradients = new ArrayList<>();
	public static List<TextureElement> defTextures = new ArrayList<>();
	public static List<RectangleElement> rects = null;
	public static List<GradientElement> gradients = null;
	public static List<TextureElement> textures = null;

	public static void reloadConfig() {
		if (!json.exists()) {
			writeConfig(new AssemblerConfig());
		}
		if (json.exists()) {
			AssemblerConfig config = null;
			try (Reader reader = new FileReader(json)) {
				config = RebornCore.GSON.fromJson(reader, AssemblerConfig.class);
				rects = config.getRects();
				gradients = config.getGradientRects();
				textures = config.getTextures();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (config == null) {
				config = new AssemblerConfig();
				writeConfig(config);
			}
		}
	}

	public static void writeConfig(AssemblerConfig config) {
		try (Writer writer = new FileWriter(json)) {
			RebornCore.GSON.toJson(config, writer);
		} catch (Exception e) {

		}
		reloadConfig();
	}

	public static enum ElementType {
		TEXTURE, RECT, GRADIENT_RECT
	}

	public static enum Layer {
		BACKGROUND, FOREGROUND
	}

	public static class AssemblerConfig {
		public List<RectangleElement> rect = defRects;
		public List<GradientElement> gradient_rect = defRectGradients;
		public List<TextureElement> texture = defTextures;

		public List<RectangleElement> getRects() {
			return rect;
		}

		public void setRect(List<RectangleElement> rect) {
			this.rect = rect;
		}

		public List<GradientElement> getGradientRects() {
			return gradient_rect;
		}

		public void setGradientRect(List<GradientElement> gradient_rect) {
			this.gradient_rect = gradient_rect;
		}

		public List<TextureElement> getTextures() {
			return texture;
		}

		public void setTexture(List<TextureElement> texture) {
			this.texture = texture;
		}
	}

	public static abstract class Element {
		public int posX;
		public int posY;
		public int width;
		public int height;
		public Layer layer;

		public Element(int posX, int posY, int width, int height, Layer layer) {
			this.posX = posX;
			this.posY = posY;
			this.width = width;
			this.height = height;
			this.layer = layer;
		}
	}

	public static class TextureElement extends Element {
		public int textureX;
		public int textureY;

		public TextureElement(int posX, int posY, int width, int height, Layer layer, int textureX, int textureY) {
			super(posX, posY, width, height, layer);
			this.textureX = textureX;
			this.textureY = textureY;
		}
	}

	public static class RectangleElement extends Element {
		public int colour;

		public RectangleElement(int posX, int posY, int width, int height, Layer layer, int colour) {
			super(posX, posY, width, height, layer);
			this.colour = colour;
		}
	}

	public static class GradientElement extends Element {
		public int startColour;
		public int endColour;

		public GradientElement(int posX, int posY, int width, int height, Layer layer, int startColour, int endColour) {
			super(posX, posY, width, height, layer);
			this.startColour = startColour;
			this.endColour = endColour;
		}
	}
}
