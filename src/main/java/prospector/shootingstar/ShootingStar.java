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
                registerItemModel(compound.getItem(), compound.getMeta(), compound.getBlockStatePath(), compound.getInventoryVariant());
                if (compound.isBlock() && !compound.getBlockStatePath().isEmpty()) {
                    ModelMethods.setBlockStateMapper(compound.getBlock(), compound.getBlockStatePath(), compound.getIgnoreProperties());
                }
            }
        }
    }
}
