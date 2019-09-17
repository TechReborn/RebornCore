package reborn.team.utils;

import java.util.Map;

public interface MappingProvider {

	String mappingName();

	String mapClass(Map<String, String> names);

	String mapMethod(Map<String, String> names, String className, String desc);

	String mapField(Map<String, String> names, String className, String desc);
}
