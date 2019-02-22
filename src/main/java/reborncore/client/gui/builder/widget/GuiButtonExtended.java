package reborncore.client.gui.builder.widget;

import net.minecraft.client.gui.GuiButton;
import org.apache.logging.log4j.util.TriConsumer;


public class GuiButtonExtended extends GuiButton {

	private TriConsumer<GuiButtonExtended, Double, Double> clickHandler;

	public GuiButtonExtended(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
		super(p_i1020_1_, p_i1020_2_, p_i1020_3_, p_i1020_4_);
	}

	public GuiButtonExtended(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_, int p_i46323_5_, String p_i46323_6_) {
		super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
	}

	public GuiButtonExtended clickHandler(TriConsumer<GuiButtonExtended, Double, Double> consumer){
		clickHandler = consumer;
		return this;
	}

	@Override
	public void onClick(double x, double y) {
		if(clickHandler != null){
			clickHandler.accept(this, x, y);
		}
		super.onClick(x, y);
	}
}
