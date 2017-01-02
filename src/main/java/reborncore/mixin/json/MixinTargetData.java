package reborncore.mixin.json;

public class MixinTargetData {

	public MixinTargetData(String mixinClass, String targetClass) {
		this.mixinClass = mixinClass;
		this.targetClass = targetClass;
	}

	public MixinTargetData() {
	}

	public String mixinClass;

	public String targetClass;

}
