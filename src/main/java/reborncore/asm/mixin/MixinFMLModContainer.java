package reborncore.asm.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

/**
 * Created by modmuss50 on 16/11/16.
 */
@Mixin(value = FMLModContainer.class)
public abstract class MixinFMLModContainer implements ModContainer {

	@Shadow
	private ModMetadata modMetadata;

	@Override
	@Overwrite
	public VersionRange acceptableMinecraftVersionRange() {
		try {
			return VersionRange.createFromVersionSpec("1.10.2");
		} catch (InvalidVersionSpecificationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		Set<ArtifactVersion> newList = Sets.newHashSet();
		for (ArtifactVersion version : modMetadata.requiredMods) {
			if (!version.getLabel().equalsIgnoreCase("forge")) {
				newList.add(version);
			}
		}
		return newList;
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		List<ArtifactVersion> newList = Lists.newArrayList();
		for (ArtifactVersion version : modMetadata.dependencies) {
			if (!version.getLabel().equalsIgnoreCase("forge")) {
				newList.add(version);
			}
		}
		return newList;
	}

}
