package reborncore.common.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class ScanDataUtils {

	public static List<ModFileScanData> getScanData(){
		return ModList.get().getAllScanData();
	}

	public static List<ModFileScanData.AnnotationData> getAnnotations(Class annotation){
		List<ModFileScanData.AnnotationData> dataList = new ArrayList<>();
		Type annotationType = Type.getType(annotation);
		for(ModFileScanData scanData : getScanData()){
			for(ModFileScanData.AnnotationData data : scanData.getAnnotations()){
				if(data.getAnnotationType() == annotationType){
					dataList.add(data);
				}
			}
		}
		return dataList;
	}


}
