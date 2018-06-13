package reborncore.common.commands;

import com.google.common.base.Charsets;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;

public class CommandListMods extends CommandBase {
	@Override
	public String getName() {
		return "rc_dumpmods";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
		Loader.instance().getActiveModList().forEach(modContainer -> stringJoiner.add(modContainer.getModId()));

		File outputFile = new File("mod_ids_export.txt");
		try {
			FileUtils.writeStringToFile(outputFile, stringJoiner.toString(), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(new TextComponentString(e.getMessage()));
			return;
		}

		sender.sendMessage(new TextComponentString("Mod list written to " + outputFile.getAbsolutePath()));
	}
}
