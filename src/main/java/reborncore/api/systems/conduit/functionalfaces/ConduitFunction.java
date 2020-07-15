package reborncore.api.systems.conduit.functionalfaces;

public enum ConduitFunction {
	EXPORT(6),
	IMPORT(6),
	BLOCK(6),
	ONE_WAY(1);

	public int max;

	ConduitFunction(int max) {
		this.max = max;
	}
}
