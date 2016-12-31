package reborncore.asm.mixin;

import javassist.*;
import javassist.bytecode.ConstPool;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import reborncore.asm.RebornLoadingCore;
import reborncore.asm.util.ClassRenamer;
import reborncore.asm.util.ConstPoolEditor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 30/12/2016.
 */
public class MixinTransfomer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (MixinManager.mixinTargetMap.containsKey(transformedName)) {
			CtClass mixinClass = null;
			try {
				mixinClass = ClassPool.getDefault().get(MixinManager.mixinTargetMap.get(transformedName));
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
			RebornLoadingCore.logHelper.info("Found mixin " + mixinClass.getName() + " for " + name);
			ClassPool cp = ClassPool.getDefault();
			cp.insertClassPath(new ByteArrayClassPath(name, basicClass));
			CtClass target = null;
			try {
				target = cp.get(name);
			} catch (NotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to apply mixin");
			}
			try {
				List<FieldData> fieldDataList = new ArrayList<>();
				for (CtField field : mixinClass.getFields()) {
					if (field.hasAnnotation(Remap.class)) {
						Remap remap = (Remap) field.getAnnotation(Remap.class);
						fieldDataList.add(new FieldData(field, field.getName(), remap.SRG()));
					}
				}
				if (RebornLoadingCore.runtimeDeobfuscationEnabled && !fieldDataList.isEmpty()) {
					ConstPool constPool = mixinClass.getClassFile().getConstPool();
					ConstPoolEditor editor = new ConstPoolEditor(constPool);
					for (int i = 1; i < constPool.getSize(); i++) {
						switch (constPool.getTag(i)) {
							case ConstPool.CONST_Fieldref: {
								for (FieldData fieldData : fieldDataList) {
									System.out.println(constPool.getFieldrefName(i));
									if (constPool.getFieldrefName(i).equals(fieldData.name)) {
										editor.changeMemberrefNameAndType(i, fieldData.srg, fieldData.field.getSignature());
									}
								}
							}
						}
					}
				}
				ClassRenamer.renameClasses(mixinClass, className -> {
					System.out.println("Trying to remap " + className);
					if (!RebornLoadingCore.runtimeDeobfuscationEnabled || !className.startsWith("net/minecraft")) {
						return null;
					}
					System.out.println("Renaming " + className + " to " + FMLDeobfuscatingRemapper.INSTANCE.unmap(className));
					return FMLDeobfuscatingRemapper.INSTANCE.unmap(className);
				});
				if (mixinClass.getClassFile().getSuperclass().startsWith("net.minecraft")) {
					System.out.println("Superclass name: " + mixinClass.getClassFile().getSuperclass());
					if (RebornLoadingCore.runtimeDeobfuscationEnabled) {
						System.out.println("Remapping super " + mixinClass.getClassFile().getSuperclass() + " to " + FMLDeobfuscatingRemapper.INSTANCE.unmap(mixinClass.getClassFile().getSuperclass().replace(".", "/")));
						mixinClass.setSuperclass(cp.get(FMLDeobfuscatingRemapper.INSTANCE.unmap(mixinClass.getClassFile().getSuperclass().replace(".", "/"))));
					}
				}
				for (CtMethod method : mixinClass.getMethods()) {
					if (method.hasAnnotation(Rewrite.class)) {
						Rewrite annotation = (Rewrite) method.getAnnotation(Rewrite.class);
						CtMethod generatedMethod = CtNewMethod.copy(method, mixinClass.getName().replace(".", "$") + "$" + method.getName(), target, null);
						target.addMethod(generatedMethod);
						CtMethod targetMethod = null;
						String targetName = RebornLoadingCore.runtimeDeobfuscationEnabled && !annotation.targetSRG().isEmpty() ? annotation.targetSRG() : annotation.target();
						String fullTargetName = null;
						if (RebornLoadingCore.runtimeDeobfuscationEnabled) {
							Map<String, String> methodMap = getMethodMap(name);
							for (Map.Entry<String, String> entry : methodMap.entrySet()) {
								//	System.out.println(entry.getKey() + "/" + entry.getValue());
								if (entry.getValue().equals(targetName)) {
									fullTargetName = entry.getKey();
									targetName = entry.getKey().split("\\(")[0];
									break;
								}
							}
						}
						for (CtMethod methodCandidate : target.getMethods()) {
							if (fullTargetName == null || fullTargetName.isEmpty()) {
								if (methodCandidate.getName().equals(targetName)) {
									targetMethod = methodCandidate;
									break;
								}
							} else {
								if ((methodCandidate.getName() + methodCandidate.getSignature()).equals(fullTargetName)) {
									targetMethod = methodCandidate;
									break;
								}
							}

						}
						if (targetMethod == null) {
							RebornLoadingCore.logHelper.error("Could not find method to inject into");
							throw new RuntimeException("Could not find method " + (RebornLoadingCore.runtimeDeobfuscationEnabled && !annotation.targetSRG().isEmpty() ? annotation.targetSRG()
							                                                                                                                                          : annotation.target()) + " to inject into");
						}
						String src = "this." + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "($$);";
						if (annotation.location().equals("START")) {
							targetMethod.insertBefore(src);
						} else if (annotation.location().equals("END")) {
							targetMethod.insertAfter(src);
						} else if (annotation.location().equals("RETURN")) {
							targetMethod.setBody(src);
						} else {
							RebornLoadingCore.logHelper.error("Could not find valid injection location.");
							throw new RuntimeException("Could not find valid injection location.");
						}
					} else if (method.hasAnnotation(Inject.class)) {
						CtMethod generatedMethod = CtNewMethod.copy(method, mixinClass.getName().replace(".", "$") + "$" + method.getName(), target, null);
						target.addMethod(generatedMethod);

					} else if (method.hasAnnotation(Constructor.class)) {
						Constructor constructor = (Constructor) method.getAnnotation(Constructor.class);
						CtMethod generatedMethod = CtNewMethod.copy(method, mixinClass.getName().replace(".", "$") + "$" + method.getName(), target, null);
						target.addMethod(generatedMethod);

						CtConstructor targetConstructor = target.getConstructor(constructor.description());
						String src = "this." + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "($$);";
						targetConstructor.insertAfter(src);
					}
				}
				for (CtField field : mixinClass.getFields()) {
					if (field.hasAnnotation(Inject.class)) {
						CtField generatedField = new CtField(field, target);
						System.out.println("Con:" + field.getConstantValue());
						target.addField(generatedField);
					}
				}
				for (CtConstructor constructor : mixinClass.getDeclaredConstructors()) {
					System.out.println(constructor.getSignature());
				}
				for (CtClass iterface : mixinClass.getInterfaces()) {
					target.addInterface(iterface);
				}
			} catch (NotFoundException | CannotCompileException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			try {
				RebornLoadingCore.logHelper.info("Successfully applied mixin to " + name);
				return target.toBytecode();
			} catch (IOException | CannotCompileException e) {
				throw new RuntimeException(e);
			}
		}

		return basicClass;
	}

	public Map<String, String> getMethodMap(String className) {
		try {
			Method method = FMLDeobfuscatingRemapper.class.getDeclaredMethod("getMethodMap", String.class);
			method.setAccessible(true);
			Map<String, String> map = (Map<String, String>) method.invoke(FMLDeobfuscatingRemapper.INSTANCE, className);
			return map;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private class FieldData {
		public CtField field;
		public String name;
		public String srg;

		public FieldData(CtField field, String name, String srg) {
			this.field = field;
			this.name = name;
			this.srg = srg;
		}
	}
}
