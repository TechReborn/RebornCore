package reborncore.jtraits;

import reborncore.jtraits.Annotation.ClosingTrait;

@Deprecated
public class MixinFactory {

	public static boolean debug = false;

	/**
	 * Makes a mixin with the specified class, and a set of mixin.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> Class<? extends T> mixin(Class<T> clazz, Class<?>... traits) {

		Class<?> cl = clazz;
		Class<?> closingTrait = null;
		do {
			ClosingTrait a = cl.getAnnotation(ClosingTrait.class);
			if (a == null)
				continue;

			try {
				closingTrait = Class.forName(a.value());
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Couldn't find the closing trait \"" + a.value() + "\"!");
			}
			break;
		} while ((cl = cl.getSuperclass()) != null && cl != Object.class);

		Class<T> c = clazz;
		for (Class<?> t : traits) {
			Mixin<T> mixin = (Mixin<T>) ClassLoadingHelper.instance.findMixin(c, t);
			if (mixin == null)
				mixin = new Mixin<T>(c, t);
			c = mixin.mixin();
		}

		if (closingTrait != null) {
			Mixin<T> mixin = (Mixin<T>) ClassLoadingHelper.instance.findMixin(c, closingTrait);
			if (mixin == null)
				mixin = new Mixin<T>(c, closingTrait);
			c = mixin.mixin();
		}

		return c;
	}

}
