package reborn.team.utils;

import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RemapJar extends DefaultTask {

	@InputFile
	File input;
	@InputFile
	File minecraftJar;
	@Input
	String from;
	@Input
	String to;
	@Input
	String mcVersion;

	@OutputFile
	File output;

	@TaskAction
	public void doTask() throws Throwable {
		if(output.exists()){
			output.delete();
		}

		getProject().getLogger().lifecycle("Remapping jar");

		Path mappings = generateMappings();

		TinyRemapper remapper = TinyRemapper.newRemapper()
			.withMappings(TinyUtils.createTinyMappingProvider(mappings, from, to))
			.renameInvalidLocals(true)
			.rebuildSourceFilenames(true)
			.build();

		if(minecraftJar.exists()){
			getProject().getLogger().lifecycle("Found minecraft jar: " + minecraftJar.getAbsolutePath());
		} else {
			throw new RuntimeException("Could not find minecraft jar");
		}

		try {
			OutputConsumerPath outputConsumer = new OutputConsumerPath(output.toPath());
			outputConsumer.addNonClassFiles(input.toPath());
			remapper.readInputs(input.toPath());

			//Add the mc jar, helps with remapping, could possibly add the libs here if issues arise
			remapper.readClassPath(minecraftJar.toPath());

			remapper.apply(outputConsumer);
			outputConsumer.close();
			remapper.finish();
		} catch (Exception e) {
			remapper.finish();
			throw new RuntimeException("Failed to remap jar", e);
		}

	}

	//Merges the mcp config mappings into a new tiny file that will be used for remapping the mod
	private Path generateMappings() throws Exception {
		Path output = new File(getProject().getBuildDir(), String.format("remaputils/%s-merged.tiny", mcVersion)).toPath();
		Files.deleteIfExists(output);

		TSRGProvider provider = new TSRGProvider("srg", extractJoined());
		TinyAppender.append(provider, getIntermediary(), output);

		if(!Files.exists(output)){
			throw new RuntimeException("Failed to merge mappings");
		}

		return output;
	}

	//Extracts the mappings out of the MCPConfig zip
	Path extractJoined() throws IOException {
		File mcpConfigZip = getMCPConfigZip();
		Path result = mcpConfigZip.toPath().getParent().resolve(mcVersion + ".tsrg");
		if(Files.exists(result)){
			return result;
		}
		try(ZipFile zipFile = new ZipFile(mcpConfigZip)){
			ZipEntry zipEntry = zipFile.getEntry("config/joined.tsrg");
			Files.copy(zipFile.getInputStream(zipEntry), result);
		}
		if(!Files.exists(result)){
			throw new RuntimeException("Could not extract mcp config");
		}
		return result;
	}

	//Find the MCPConfig zip
	File getMCPConfigZip(){
		File userCache = new File(getProject().getGradle().getGradleUserHomeDir(), "caches" + File.separator + "forge_gradle");
		File mcpConfig = new File(userCache, String.format("maven_downloader/de/oceanlabs/mcp/mcp_config/%s/mcp_config-%s.zip", mcVersion, mcVersion));
		if(!mcpConfig.exists()){
			throw new RuntimeException("Could not find mcp config");
		}
		return mcpConfig;
	}


	//Downloads the intermediary mappings
	Path getIntermediary() throws IOException {
		File intermediary = new File(getProject().getBuildDir(), String.format("remaputils/%s.tiny", mcVersion));
		if(intermediary.exists()){
			return intermediary.toPath();
		}
		//Easier to download from github, over extracting a jar
		URL url = new URL(String.format("https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/%s.tiny", mcVersion));
		FileUtils.copyURLToFile(url, intermediary);
		if(!intermediary.exists()){
			throw new RuntimeException("Could not download intermediary");
		}
		return intermediary.toPath();
	}

	public void setInput(File input) {
		this.input = input;
	}

	public void setMinecraftJar(File minecraftJar) {
		this.minecraftJar = minecraftJar;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setMcVersion(String mcVersion) {
		this.mcVersion = mcVersion;
	}
}
