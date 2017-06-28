/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.mixin.transformer;

import javassist.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.mixin.MixinManager;
import reborncore.mixin.api.Constructor;
import reborncore.mixin.api.Inject;
import reborncore.mixin.api.Rewrite;
import reborncore.mixin.implementations.forge.MixinForgeLoadingCore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is where most of it happens.
 */
public class MixinTransformer implements IClassTransformer {

	public static ClassPool cp = new ClassPool(true);
	public static List<String> preLoadedClasses = new ArrayList<>();

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (MixinManager.mixinTargetMap.containsKey(transformedName)) {
			//Start of support for sponge mixins
			//This fixes a crash when a reborn core mixin, mixes the same thing that sponge wants to.
			try {
				MixinManager.logger.trace("Mixin Transformer being called by " + Thread.currentThread().getStackTrace()[3].getClassName());
				if (Thread.currentThread().getStackTrace()[3].getClassName().equals("org.spongepowered.asm.mixin.transformer.TreeInfo")) { //TODO check for repackages of the sponge mixin lib
					MixinManager.logger.trace("Skipping mixin transformer as it is being called by Sponge.");
					return basicClass;
				}
			} catch (Exception e) {

			}
			//This should not happen, just stop it from doing it anyway.
			if (MixinManager.transformedClasses.contains(name)) {
				MixinManager.logger.trace("Skipping mixin transformer as the transformer has already transformed this class");
				return basicClass;
			}
			//End support

			long start = System.currentTimeMillis();

			if(!preLoadedClasses.contains(name)){
				cp.insertClassPath(new ByteArrayClassPath(name, basicClass));
			}

			CtClass target = null;
			try {
				target = cp.get(name);
			} catch (NotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to generate target infomation");
			}
			if (target.isFrozen()) {
				target.defrost();
			}
			if (!transformedName.startsWith("net.minecraft")) {
				MixinManager.mixinRemaper.remap(target, cp);
			}

			List<String> mixins = MixinManager.mixinTargetMap.get(transformedName);
			//Removes duplicate entries
			mixins = mixins.stream().distinct().collect(Collectors.toList());

			MixinManager.logger.info("Found " + mixins.size() + " mixins for " + transformedName);
			for (String mixinClassName : mixins) {
				CtClass mixinClass = null;
				try {
					//loads the mixin class
					mixinClass = cp.get(mixinClassName);
				} catch (NotFoundException e) {
					throw new RuntimeException(e);
				}
				try {
					//Remaps the mixin class
					MixinManager.mixinRemaper.remap(mixinClass, cp);
					for (CtMethod method : mixinClass.getMethods()) {
						if (method.hasAnnotation(Rewrite.class)) {
							Rewrite annotation = (Rewrite) method.getAnnotation(Rewrite.class);
							//Copy's the mixin method to a new method targeting the target
							//This also renames the methord to contain the classname of the mixin
							CtMethod generatedMethod = CtNewMethod.copy(method, mixinClass.getName().replace(".", "$") + "$" + method.getName(), target, null);
							target.addMethod(generatedMethod);
							CtMethod targetMethod = null;
							Optional<Pair<String, String>> remappedTargetInfo = MixinManager.mixinRemaper.getFullTargetName(annotation, name);
							for (CtMethod methodCandidate : target.getMethods()) {
								if (!remappedTargetInfo.isPresent()) {
									if (methodCandidate.getName().equals(annotation.target()) && methodCandidate.getSignature().equals(method.getSignature())) {
										targetMethod = methodCandidate;
										break;
									}
								} else {
									if ((methodCandidate.getName() + methodCandidate.getSignature()).equals(remappedTargetInfo.get().getRight())) {
										targetMethod = methodCandidate;
										break;
									}
								}

							}
							if (targetMethod == null) {
								MixinManager.logger.error("Could not find method to inject into");
								throw new RuntimeException("Could not find method " + (MixinForgeLoadingCore.runtimeDeobfuscationEnabled && !annotation.targetSRG().isEmpty() ? annotation.targetSRG()
								                                                                                                                                              : annotation.target()) + " to inject into");
							}
							//This generates the one line of code that calls the new method that was just injected above

							String src = null;
							String mCall = annotation.isStatic() ? "" : "this.";
							switch (annotation.returnBehavor()) {
								case NONE:
									src = mCall + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "($$);";
									break;
								case OBJECT_NONE_NULL:
									src = "Object mixinobj = " + mCall + generatedMethod.getName() + "($$);" + "if(mixinobj != null){return ($w)mixinobj;}";
									break;
								case BOOL_TRUE:
									if (!method.getReturnType().getName().equals("boolean")) {
										throw new RuntimeException(method.getName() + " does not return a boolean");
									}
									if (targetMethod.getReturnType().getName().equals("boolean")) {
										src = "if(" + mCall + generatedMethod.getName() + "($$)" + "){return true;}";
										break;
									}
									src = "if(" + mCall + generatedMethod.getName() + "($$)" + "){return;}";
									break;
								default:
									src = mCall + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "($$);";
									break;
							}

							//Adds it into the correct location
							switch (annotation.behavior()) {
								case START:
									targetMethod.insertBefore(src);
									break;
								case END:
									targetMethod.insertAfter(src);
									break;
								case REPLACE:
									targetMethod.setBody(src);
									break;
							}

						} else if (method.hasAnnotation(Inject.class)) {
							//Just copys and adds the method stright into the target class
							String methodName = method.getName();
							Inject inject = (Inject) method.getAnnotation(Inject.class);
							if (inject.rename()) {
								methodName = mixinClass.getName().replace(".", "$") + "$" + method.getName();
							}
							CtMethod generatedMethod = CtNewMethod.copy(method, methodName, target, null);
							target.addMethod(generatedMethod);
						} else if (method.hasAnnotation(Constructor.class)) {
							//Creates a new method with the same naming style as rewrite
							Constructor constructor = (Constructor) method.getAnnotation(Constructor.class);
							CtMethod generatedMethod = CtNewMethod.copy(method, mixinClass.getName().replace(".", "$") + "$" + method.getName(), target, null);
							target.addMethod(generatedMethod);

							//Calls the new method at the end of the specified constructor
							CtConstructor targetConstructor = target.getConstructor(constructor.signature());
							String src = "this." + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "($$);";
							targetConstructor.insertAfter(src);
						}
					}
					for (CtField field : mixinClass.getFields()) {
						//Copy's the field over
						if (field.hasAnnotation(Inject.class)) {
							CtField generatedField = new CtField(field, target);
							target.addField(generatedField);
						}
					}
					//Adds all the interfaces from the mixin class to the target
					for (CtClass iface : mixinClass.getInterfaces()) {
						target.addInterface(iface);
					}
					for (CtConstructor constructor : mixinClass.getConstructors()) {
						if (constructor.hasAnnotation(Inject.class)) {
							CtConstructor generatedConstructor = CtNewConstructor.copy(constructor, target, null);
							target.addConstructor(generatedConstructor);
						}
					}
				} catch (NotFoundException | CannotCompileException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				MixinManager.logger.info("Successfully applied " + mixinClassName + " to " + name);
			}
			try {
				MixinManager.logger.info("Successfully applied " + mixins.size() + " mixins to " + name + " in " + (System.currentTimeMillis() - start) + "ms");
				MixinManager.transformedClasses.add(name);
				return target.toBytecode();
			} catch (IOException | CannotCompileException e) {
				throw new RuntimeException(e);
			}
		}

		return basicClass;
	}
}
