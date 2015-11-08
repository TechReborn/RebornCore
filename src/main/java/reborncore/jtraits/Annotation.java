package reborncore.jtraits;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotation {

    /**
     * Can be applied to classes.<br/>
     * <br/>
     * When a mixin uses the class this is applied to as the base, JTraits will make sure the trait {@link #value()} is applied at the end.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ClosingTrait {

        /**
         * The JTrait that will be applied last when making a mixin with this class.
         */
        String value();
    }

    /**
     * Can be applied to classes.<br/>
     * <br/>
     * When a mixin uses the class this is applied to as the base, JTraits will try to set the boolean variable {@link #value()} to true.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CheckMixin {

        /**
         * The field that will be set to true when this class is part of a mixin.
         */
        String value();
    }

}
