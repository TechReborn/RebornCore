package reborncore.common.util;

//A basic color class that is used in place of the AWT color class as it cannot be used with lwjgl 3
public class Color {

	public final static Color WHITE = new Color(255, 255, 255);
	public final static Color RED = new Color(255, 0, 0);
	public final static Color GREEN = new Color(0, 255, 0);
	public final static Color BLUE = new Color(0, 0, 255);

	private final int color;

	public Color(int r, int g, int b, int a) {
		color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
	}

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(float r, float g, float b) {
		this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
	}

	public int getColor() {
		return color;
	}

	public int getRed() {
		return (getColor() >> 16) & 0xFF;
	}

	public int getGreen() {
		return (getColor() >> 8) & 0xFF;
	}

	public int getBlue() {
		return getColor() & 0xFF;
	}

	public int getAlpha() {
		return (getColor() >> 24) & 0xff;
	}

	public Color darker() {
		double amount = 0.7;
		return new Color((int) (getRed() * amount), (int) (getGreen() * amount), (int) (getBlue() * amount), getAlpha());
	}
}
