package reborncore.common.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import reborncore.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

//This command was added to test something, it was easier to put here than make a new mod for it. and some people may find it usefull I hope.
public class CommandListRecipes extends CommandBase {
	@Override
	public String getName() {
		return "rc_listRecipes";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "rc_listRecipes";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer)){
			sender.sendMessage(new TextComponentString("Command must be ran as a player!"));
			return;
		}
		EntityPlayer player = (EntityPlayer) sender;
		if(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()){
			sender.sendMessage(new TextComponentString("You must be holding an item in your main hand"));
			return;
		}
		List<IRecipe> recipeList = new ArrayList<>();
		CraftingManager.REGISTRY.forEach(recipe -> {
			if(ItemUtils.isItemEqual(recipe.getRecipeOutput(), player.getHeldItem(EnumHand.MAIN_HAND), true, true, true)){
				recipeList.add(recipe);
			}
		});
		if(recipeList.isEmpty()){
			sender.sendMessage(new TextComponentString("No recipes found. Does this item have a recipe? Is this item using the vanilla crafting manager?"));
			return;
		}
		sender.sendMessage(new TextComponentString("Found " + recipeList.size() + " recipes"));
		recipeList.forEach(recipe -> sender.sendMessage(new TextComponentString(recipe.getRegistryName().toString())));
	}
}
