package prospector.shootingstar;

import prospector.shootingstar.model.ModelCompound;
import prospector.shootingstar.model.ModelMethods;

import java.util.ArrayList;
import java.util.List;

import static prospector.shootingstar.model.ModelMethods.registerItemModel;

public class ShootingStar {
	protected static List<ModelCompound> modelList = new ArrayList<>();

	public static void registerModel(ModelCompound modelCompound) {
		modelList.add(modelCompound);
	}

	public static void registerModels(String modid) {
		for (ModelCompound compound : modelList) {
			if (compound.getModid().equals(modid)) {
				if (compound.isBlock()) {
					if (compound.getFileName().equals("shootingstar.undefinedfilename"))
						registerItemModel(compound.getItem(), compound.getMeta(), compound.getBlockStatePath(), compound.getInventoryVariant());
					else
						registerItemModel(compound.getItem(), compound.getMeta(), compound.getFileName(), compound.getBlockStatePath(), compound.getInventoryVariant());
				}
				if (compound.isBlock()) {
					if (compound.getFileName().equals("shootingstar.undefinedfilename"))
						ModelMethods.setBlockStateMapper(compound.getBlock(), compound.getBlockStatePath(), compound.getIgnoreProperties());
					else
						ModelMethods.setBlockStateMapper(compound.getBlock(), compound.getFileName(), compound.getBlockStatePath(), compound.getIgnoreProperties());
				}
			}
		}
	}
}
