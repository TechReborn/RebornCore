package chylex.reborncore.javacheck.util;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;

public final class ForgeCompatibility{
	public static boolean tryLog(String data){
		try{
			org.apache.logging.log4j.LogManager.getLogger("JavaChecker").log(org.apache.logging.log4j.Level.ERROR,data);
			return true;
		}catch(Throwable t){ // apache logging is not available
			t.printStackTrace();
		}
		
		try{
			Class relaunchLog = findFMLClass("relaunch","FMLRelaunchLog");
			Method logSevere = findMethod(relaunchLog,"severe",String.class,Object[].class);
			
			if (logSevere != null){
				logSevere.invoke(null,data,new Object[0]);
				return true;
			}
		}catch(Throwable t){ // relaunch log not available
			t.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean tryResetModState(){
		try{
			Class cmm = findFMLClass("relauncher","CoreModManager");
			Method getCoremods = findMethodAlt(cmm,new String[]{ "getLoadedCoremods", "getIgnoredMods" });
			Method getReparsed = findMethodAlt(cmm,new String[]{ "getReparseableCoremods" });
			
			if (getCoremods == null || getReparsed == null)return false;
			
			String myFile = getModFileName();
			
			((List)getCoremods.invoke(null)).remove(myFile);
			((List)getReparsed.invoke(null)).add(myFile);
			return true;
		}catch(Throwable t){
			t.printStackTrace();
			return false;
		}
	}
	
	public static boolean isClientSide(){
		try{
			return Class.forName("net.minecraft.client.main.Main") != null;
		}catch(ClassNotFoundException e){
			return false;
		}catch(Throwable t){
			t.printStackTrace();
			return false;
		}
	}
	
	private static Class findFMLClass(String classPackage, String className){
		String searchTarget = classPackage.isEmpty() ? className : classPackage+"."+className;
		
		try{
			return Class.forName("cpw.mods.fml."+searchTarget);
		}catch(ClassNotFoundException e){}
		
		try{
			return Class.forName("net.minecraftforge.fml."+searchTarget);
		}catch(ClassNotFoundException e){}
		
		return null;
	}
	
	private static Method findMethod(Class cls, String methodName, Class...params){
		if (cls == null)return null;
		
		try{
			return cls.getMethod(methodName,params);
		}catch(NoSuchMethodException e){}
		
		return null;
	}
	
	private static Method findMethodAlt(Class cls, String[] methodNames, Class...params){
		if (cls == null)return null;
		
		for(String methodName:methodNames){
			try{
				return cls.getMethod(methodName,params);
			}catch(NoSuchMethodException e){}
		}
		
		return null;
	}
	
	private static String getModFileName() throws URISyntaxException{
		return new File(ForgeCompatibility.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName();
	}
}
