package reborn.team.utils;

import reborn.team.utils.MappingProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TSRGProvider implements MappingProvider {

	private String name;
	private HashMap<String, String> classes = new HashMap<>();
	private HashMap<String, HashMap<String, String>> methods = new HashMap<>();
	private HashMap<String, HashMap<String, String>> fields = new HashMap<>();

	public TSRGProvider(String name, Path path) throws IOException {
		this.name = name;

		String classMapping = null;
		for (String line : Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList())) {
			if (line.startsWith("\t")) {
				if (classMapping == null) { //We must have a class
					throw new RuntimeException("Invalid mappings format");
				}
				String[] split = line.trim().split(" ");
				if (split.length == 2) { //Field
					fields.get(classMapping).put(split[0], split[1]);
				} else if (split.length == 3) { //Method
					methods.get(classMapping).put(split[0] + split[1], split[2]);
				} else {
					throw new RuntimeException("Invalid mappings format");
				}

			} else {
				String[] split = line.trim().split(" ");
				if (split.length != 2) {
					throw new RuntimeException("unexpected class fortmat");
				}
				classMapping = split[0];
				classes.put(split[0], split[1]);
				methods.put(split[0], new HashMap<>());
				fields.put(split[0], new HashMap<>());
			}
		}
	}

	@Override
	public String mappingName() {
		return name;
	}

	@Override
	public String mapClass(Map<String, String> names) {
		return classes.get(names.get("official"));
	}

	@Override
	public String mapMethod(Map<String, String> names, String className, String desc) {
		return methods.get(className).get(names.get("official") + desc);
	}

	@Override
	public String mapField(Map<String, String> names, String className, String desc) {
		return fields.get(className).get(names.get("official"));
	}
}
