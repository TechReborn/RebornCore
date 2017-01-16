package reborncore.modcl;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by Prospector
 */
public class BlockCL extends Block {
	public ModCL mod;
	public String name;

	public BlockCL(ModCL mod, String name, Material material) {
		super(material);
		this.mod = mod;
		this.name = name;
		setUnlocalizedName(mod.getPrefix() + name);
		setRegistryName(mod.getModID(), name);
	}

	public BlockCL(String name, ModCL mod) {
		this(mod, name, Material.ROCK);
	}
}
