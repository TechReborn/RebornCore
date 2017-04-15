package reborncore.modcl;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by Prospector
 */
public class BlockCL extends Block {
	public ModCL mod;
	public String name;

	public BlockCL(ModCL mod, String name, Material material, boolean registerModel) {
		super(material);
		setInfo(mod, name);
		if (registerModel) {
			mod.blockModelsToRegister.add(this);
		}
	}

	public BlockCL(ModCL mod, String name, boolean registerModel) {
		this(mod, name, Material.ROCK, registerModel);
	}

	public BlockCL(ModCL mod, String name, Material material) {
		this(mod, name, material, true);
	}

	public BlockCL(ModCL mod, String name) {
		this(mod, name, Material.ROCK, true);
	}

	private void setInfo(ModCL mod, String name) {
		this.mod = mod;
		this.name = name;
		setUnlocalizedName(mod.getPrefix() + name);
		setRegistryName(mod.getModID(), name);
		setCreativeTab(mod.getTab());
	}
}
