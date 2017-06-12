package reborncore.modcl;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Prospector
 */
public abstract class BlockContainerCL extends BlockContainer {
	public ModCL mod;
	public String name;
	public Class<? extends TileEntity> tileEntity;

	public BlockContainerCL(ModCL mod, String name, Material material, boolean registerModel, Class<? extends TileEntity> tileEntity) {
		super(material);
		setInfo(mod, name, tileEntity);
		if (registerModel) {
			mod.blockModelsToRegister.add(this);
		}
	}

	public BlockContainerCL(ModCL mod, String name, boolean registerModel, Class<? extends TileEntity> tileEntity) {
		this(mod, name, Material.ROCK, registerModel, tileEntity);
	}

	public BlockContainerCL(ModCL mod, String name, Material material, Class<? extends TileEntity> tileEntity) {
		this(mod, name, material, true, tileEntity);
	}

	public BlockContainerCL(ModCL mod, String name, Class<? extends TileEntity> tileEntity) {
		this(mod, name, Material.ROCK, true, tileEntity);
	}

	private void setInfo(ModCL mod, String name, Class<? extends TileEntity> tileEntity) {
		this.mod = mod;
		this.name = name;
		this.tileEntity = tileEntity;
		setUnlocalizedName(mod.getPrefix() + name);
		setRegistryName(mod.getModID(), name);
		setCreativeTab(mod.getTab());
	}
}
