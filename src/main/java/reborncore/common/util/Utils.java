package reborncore.common.util;

import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Utils {
    public static final Set<EnumFacing> NO_FACINGS = Collections.emptySet();
    public static final Set<EnumFacing> HORIZONTAL_FACINGS = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(EnumFacing.HORIZONTALS)));
    public static final Set<EnumFacing> VERTICAL_FACINGS = Collections.unmodifiableSet(EnumSet.of(EnumFacing.DOWN, EnumFacing.UP));
    public static final Set<EnumFacing> ALL_FACINGS = Collections.unmodifiableSet(EnumSet.allOf(EnumFacing.class));
}
