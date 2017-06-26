package reborncore.mixin.api;

import reborncore.mixin.json.MixinTargetData;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by modmuss50 on 26/06/2017.
 */
public interface IMixinRegistry {

	default @Nonnull
	MixinRegistationTime registrationTime() {
		return MixinRegistationTime.EARLY;
	}

	List<MixinTargetData> register();

}
