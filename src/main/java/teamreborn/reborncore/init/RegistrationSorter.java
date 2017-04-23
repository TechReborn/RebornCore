package teamreborn.reborncore.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mark on 23/04/2017.
 */
public class RegistrationSorter {

	//TODO fix this as its the most janky thing that works ever
	public static void sort(List<RegistrationManager.RegistryFactoryInfo> list){
		Collections.sort(list, Comparator.comparing(o -> o.registredClassName));

		Collections.sort(list, (o1, o2) -> {
			if(!o1.loadOrder.isEmpty() && o1.loadOrder.startsWith("before:")){
				String className = o1.loadOrder.replace("before:", "");
				if(o2.registredClassName.equalsIgnoreCase(className)){
					return -1;
				}
			}
			if(!o2.loadOrder.isEmpty() && o2.loadOrder.startsWith("after:")){
				String className = o2.loadOrder.replace("after:", "");
				if(o1.registredClassName.equalsIgnoreCase(className)){
					return -1;
				}
			}
			return 0;
		});
	}

	//Shitty test code

	public static void main(String[] args) {
		List<RegistrationManager.RegistryFactoryInfo> info = new ArrayList<>();
		info.add(new RegistrationManager.RegistryFactoryInfo(null, null, null, "after:123", "first"));
		info.add(new RegistrationManager.RegistryFactoryInfo(null, null, null, "", "test"));
		info.add(new RegistrationManager.RegistryFactoryInfo(null, null, null, "before:test", "123"));

		//list(info);
		sort(info);
		System.out.println();
		System.out.println();
		list(info);
	}

	private static void list(List<RegistrationManager.RegistryFactoryInfo> infos){
		for(RegistrationManager.RegistryFactoryInfo info : infos){
			System.out.println(info.registredClassName);
		}
	}

}
