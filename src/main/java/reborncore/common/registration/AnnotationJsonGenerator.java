package reborncore.common.registration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

//Quick file used to generate a json containing all of the annotation data in a class
public class AnnotationJsonGenerator {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void main(String[] args) throws IOException {
		File input = new File(args[0]);
		if(!input.exists()){
			throw new FileNotFoundException("Could not find " + args[0]);
		}
		File output = new File(args[1]);
		if(output.exists()){
			output.delete();
		}

		AnnotationModel annotationModel = new AnnotationModel();

		JarFile jarFile = new JarFile(input);
		Enumeration<JarEntry> entries = jarFile.entries();
		while(entries.hasMoreElements()){
			JarEntry jarEntry = entries.nextElement();
			if(jarEntry.getName().endsWith(".class")){
				try(InputStream stream = jarFile.getInputStream(jarEntry)){
					readClass(stream, annotationModel);
				}
			}
		}

		FileUtils.writeStringToFile(output, GSON.toJson(annotationModel), StandardCharsets.UTF_8);

	}

	private static void readClass(InputStream stream, AnnotationModel annotationModel) throws IOException {
		ClassNode classNode = readClassFromBytes(IOUtils.toByteArray(stream));
		if(annotationModel.classes.containsKey(classNode.name)){
			throw new RuntimeException("Class data already exists for " + classNode.name);
		}

		System.out.println("Reading: " + classNode.name);

		AnnotationModel.ClassData classData = new AnnotationModel.ClassData();
		classData.className = classNode.name;

		classData.annotations = classNode.visibleAnnotations;

		classData.methods = classNode.methods.stream()
			.filter(methodNode -> methodNode.visibleAnnotations != null && !methodNode.visibleAnnotations.isEmpty())
			.map(methodNode -> {
				AnnotationModel.MethodData methodData = new AnnotationModel.MethodData();
				methodData.access = methodNode.access;
				methodData.name = methodNode.name;
				methodData.desc = methodNode.desc;
				methodData.annotations = methodNode.visibleAnnotations;
				return methodData;
			}).collect(Collectors.toList());

		classData.fields = classNode.fields.stream()
			.filter(fieldNode -> fieldNode.visibleAnnotations != null && !fieldNode.visibleAnnotations.isEmpty())
			.map(fieldNode -> {
				AnnotationModel.FieldData fieldData = new AnnotationModel.FieldData();
				fieldData.access = fieldNode.access;
				fieldData.name = fieldNode.name;
				fieldData.desc = fieldNode.desc;
				fieldData.annotations = fieldNode.visibleAnnotations;
				return fieldData;
			}).collect(Collectors.toList());


		//Only write out classes that have annotations
		if(classData.fields.isEmpty()){
			classData.fields = null;
		}
		if(classData.methods.isEmpty()){
			classData.methods = null;
		}

		if(classData.methods != null || classData.fields != null || classData.annotations != null){
			annotationModel.classes.put(classNode.name, classData);
		}


	}

	private static ClassNode readClassFromBytes(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

}
