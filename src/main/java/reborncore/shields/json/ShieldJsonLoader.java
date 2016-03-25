package reborncore.shields.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.io.FileUtils;

import reborncore.shields.FaceShield;
import reborncore.shields.api.ShieldRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Mark on 25/03/2016.
 */
public class ShieldJsonLoader
{

	public static boolean hasValidJsonFile;
	@Nullable
	public static ShieldJsonFile shieldJsonFile;

	/**
	 * This is used to make the json file
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		ShieldJsonFile jsonFile = new ShieldJsonFile();
		jsonFile.userList = new ArrayList<>();
		File folder = new File("src/main/resources/assets/reborncore/textures/shields/people/");
		System.out.println(folder.getAbsolutePath());
		for (File person : folder.listFiles())
		{
			jsonFile.userList.add(new ShieldUser(person.getName().replace(".png", ""), getMD5(person)));
		}
		File output = new File("shields.json");
		System.out.println(output.getAbsolutePath());

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(jsonFile);
		try
		{
			FileWriter writer = new FileWriter(output);
			writer.write(json);
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void load(FMLPreInitializationEvent event) throws IOException
	{
		File file = new File(event.getModConfigurationDirectory(), "reborncore/shields.json");
		FileUtils.copyURLToFile(new URL("http://modmuss50.me/reborncore/shields.json"), file);
		if (file.exists())
		{
			Gson gson = new Gson();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Type typeOfHashMap = new TypeToken<ShieldJsonFile>()
			{
			}.getType();
			shieldJsonFile = gson.fromJson(reader, typeOfHashMap);
			hasValidJsonFile = true;
		}
		if (shieldJsonFile != null)
		{
			for (ShieldUser user : shieldJsonFile.userList)
			{
				ShieldRegistry.registerShield(new FaceShield(user.username));
			}
		}
	}

	public static String getMD5(File file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}

}
