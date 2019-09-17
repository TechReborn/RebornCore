package reborn.team.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class TinyAppender {

	public static void append(MappingProvider provider, Path input, Path output) throws IOException {
		String[] mappingTypes = null;
		List<String> replacementLines = new ArrayList<>();
		for (String s : Files.lines(input, StandardCharsets.UTF_8).collect(Collectors.toList())) {
			StringBuilder lineBuilder = new StringBuilder();
			lineBuilder.append(s);

			if (s.startsWith("#")) {
				continue;
			}

			String[] split = s.split("\t");
			if (mappingTypes == null) { //First line
				if (!split[0].equals("v1")) {
					throw new RuntimeException("unsupported tiny file");
				}
				mappingTypes = Arrays.stream(split).filter(s1 -> !s1.equals("v1")).toArray(String[]::new);
				lineBuilder.append("\t");
				lineBuilder.append(provider.mappingName());
			} else {

				switch (split[0]) {
					case "CLASS": {
						Map<String, String> names = getNamesMap(mappingTypes, split, 1);
						String name = provider.mapClass(names);
						if (name == null || name.isEmpty()) {
							name = names.get("official");
							System.out.println("No class name provided for " + name);
						}
						lineBuilder.append("\t");
						lineBuilder.append(name);
						break;
					}
					case "METHOD": {
						Map<String, String> names = getNamesMap(mappingTypes, split, 3);
						String name = provider.mapMethod(names, split[1], split[2]);
						if (name == null || name.isEmpty()) {
							name = names.get("official");
							System.out.println("No method name provided for " + name);
						}
						lineBuilder.append("\t");
						lineBuilder.append(name);
						break;
					}
					case "FIELD": {
						Map<String, String> names = getNamesMap(mappingTypes, split, 3);
						String name = provider.mapField(names, split[1], split[2]);
						if (name == null || name.isEmpty()) {
							name = names.get("official");
							System.out.println("No field name provided for " + name);
						}
						lineBuilder.append("\t");
						lineBuilder.append(name);
						break;
					}
					default:
						throw new RuntimeException("Unsupported mapping format:" + split[0]);
				}
			}

			replacementLines.add(lineBuilder.toString());
		}

		Files.write(output, replacementLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	}

	private static Map<String, String> getNamesMap(String[] mappingTypes, String[] split, int start) {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < mappingTypes.length; i++) {
			map.put(mappingTypes[i], split[i + start]);
		}
		return map;
	}

}
