package reborncore.common.registration;

import org.objectweb.asm.tree.AnnotationNode;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnotationModel {

	public Map<String, ClassData> classes = new HashMap<>();

	public static class ClassData {
		public String className;
		List<AnnotationNode> annotations;
		List<MethodData> methods;
		List<FieldData> fields;
	}

	public static class MethodData {
		public int access;
		public String name;
		public String desc;

		List<AnnotationNode> annotations;
	}

	public static class FieldData {
		public int access;
		public String name;
		public String desc;

		List<AnnotationNode> annotations;
	}

	public List<ClassData> findClasses(Class<? extends Annotation> annotation){
		String annotationDesc = String.format("L%s;", annotation.getName().replaceAll("\\.", "/"));
		System.out.println(annotationDesc);
		return classes.values().stream()
			.filter(classData -> classData.annotations != null && classData.annotations.stream()
				.anyMatch(annotationNode -> annotationNode.desc.equals(annotationDesc))
			).collect(Collectors.toList());

	}

}
