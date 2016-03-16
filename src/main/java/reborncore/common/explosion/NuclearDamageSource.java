package reborncore.common.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * Created by modmuss50 on 16/03/2016.
 */
public class NuclearDamageSource extends EntityDamageSource {
    public NuclearDamageSource(Entity entity) {
        super("nuke", entity);
    }
}
