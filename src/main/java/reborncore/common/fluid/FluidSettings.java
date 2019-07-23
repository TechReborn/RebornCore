package reborncore.common.fluid;

import net.minecraft.util.Identifier;

public class FluidSettings {

	private Identifier flowingTexture = new Identifier("reborncore:nope");
	private Identifier stillTexture = new Identifier("reborncore:nope");

	public FluidSettings setFlowingTexture(Identifier flowingTexture) {
		this.flowingTexture = flowingTexture;
		return this;
	}

	public FluidSettings setStillTexture(Identifier stillTexture) {
		this.stillTexture = stillTexture;
		return this;
	}

	public Identifier getFlowingTexture() {
		return flowingTexture;
	}

	public Identifier getStillTexture() {
		return stillTexture;
	}

	private FluidSettings() {
	}

	public static FluidSettings create(){
		return new FluidSettings();
	}

}
