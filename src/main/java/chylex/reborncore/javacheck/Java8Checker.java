package chylex.reborncore.javacheck;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.commons.lang3.JavaVersion;

public final class Java8Checker implements ITweaker
{
	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader)
	{
		JavaVersionChecker.run(JavaVersion.JAVA_1_8);
	}

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
	{
	}

	@Override
	public String getLaunchTarget()
	{
		return null;
	}

	@Override
	public String[] getLaunchArguments()
	{
		return new String[0];
	}
}
