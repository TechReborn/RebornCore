package reborncore.asm.mixin;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Mixin {

	String target();

}
