package reborncore.jtraits;

public class JTrait<T>
{

	/**
	 * A reference to the class this trait was applied to.
	 */
	public T _super;

	/**
	 * A reference to the final class, with this trait applied to it (and all
	 * the others, if any).
	 */
	public T _self;

}
