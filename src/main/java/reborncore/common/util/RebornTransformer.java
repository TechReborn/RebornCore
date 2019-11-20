/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package reborncore.common.util;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//Having this DOES NOT make reborn core a core mod, it just has code to help other core mods
//This is a basic wrapper around asm to allow for lamaba class transformers
public class RebornTransformer implements IClassTransformer {

	List<ClassTransformer> classTransformers = new ArrayList<>();

	public ClassTransformer transformClass(String className) {
		ClassTransformer classTransformer = new ClassTransformer(className);
		classTransformers.add(classTransformer);
		return classTransformer;
	}

	public static class ClassTransformer {

		String name;
		List<MethodTransformer> methodTransformers = new ArrayList<>();

		public ClassTransformer(String name) {
			this.name = name;
		}

		public MethodTransformer findMethod(String name, String desc) {
			MethodTransformer methodTransformer = new MethodTransformer(name, desc);
			methodTransformers.add(methodTransformer);
			return methodTransformer;
		}

		private List<MethodTransformer> getMethodTransformers(MethodNode methodNode) {
			return methodTransformers
				.stream()
				.filter(methodTransformer -> methodTransformer.name.equals(methodNode.name) && methodTransformer.desc.equals(methodNode.desc)) //TODO handle srg names in a nice way ;)
				.collect(Collectors.toList());
		}

		private void handle(ClassNode classNode) {
			classNode.methods
				.forEach(methodNode ->
					getMethodTransformers(methodNode)
						.forEach(methodTransformer -> methodTransformer.handle(methodNode))
				);
		}

	}

	public static class MethodTransformer {

		String name;
		String desc;

		ClassTransformer classTransformer;
		List<Consumer<MethodNode>> methodTransformers = new ArrayList<>();

		public MethodTransformer(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}

		public MethodTransformer(ClassTransformer classTransformer) {
			this.classTransformer = classTransformer;
		}

		public ClassTransformer getClassTransformer() {
			return classTransformer;
		}

		public MethodTransformer transform(Consumer<MethodNode> methodNodeConsumer) {
			methodTransformers.add(methodNodeConsumer);
			return this;
		}

		private void handle(MethodNode methodNode) {
			methodTransformers.forEach(methodNodeConsumer -> methodNodeConsumer.accept(methodNode));
		}
	}

	public List<ClassTransformer> getTransformers(String className) {
		return classTransformers
			.stream()
			.filter(classTransformer -> classTransformer.name.equals(className))
			.collect(Collectors.toList());
	}

	//Implimentation :D

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		List<ClassTransformer> classTransformers = getTransformers(name);
		if (!classTransformers.isEmpty()) {
			ClassNode classNode = readClassFromBytes(basicClass);

			classTransformers.forEach(classTransformer -> classTransformer.handle(classNode));

			return writeClassToBytes(classNode);
		}
		return basicClass;
	}

	public static ClassNode readClassFromBytes(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	public static byte[] writeClassToBytes(ClassNode classNode) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
