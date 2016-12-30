package reborncore.asm.mixin;

import com.google.common.base.Joiner;
import javassist.*;
import javassist.bytecode.ConstPool;
import net.minecraft.launchwrapper.IClassTransformer;
import reborncore.asm.RebornLoadingCore;
import reborncore.asm.util.ConstPoolEditor;
import reborncore.common.network.PacketWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
			cp.insertClassPath(new ByteArrayClassPath(transformedName, basicClass));
			CtClass target = null;
			try {
				target = cp.get(transformedName);
			} catch (NotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to apply mixin");
			}
			try {
				List<FieldData> fieldDataList = new ArrayList<>();
				for(CtField field : mixinClass.getFields()){
					if(field.hasAnnotation(Remap.class)){
						Remap remap = (Remap) field.getAnnotation(Remap.class);
						fieldDataList.add(new FieldData(field, field.getName(), remap.SRG()));
					}
				}
				if(RebornLoadingCore.runtimeDeobfuscationEnabled && !fieldDataList.isEmpty()){
					ConstPool constPool = mixinClass.getClassFile().getConstPool();
					ConstPoolEditor editor = new ConstPoolEditor(constPool);
					for (int i = 1; i < constPool.getSize(); i++) {
						switch (constPool.getTag(i)){
							case ConstPool.CONST_Fieldref: {
								for(FieldData fieldData : fieldDataList){
									System.out.println(constPool.getFieldrefName(i));
									if(constPool.getFieldrefName(i).equals(fieldData.name)){
										editor.changeMemberrefNameAndType(i, fieldData.srg, fieldData.field.getSignature());
									}
								}
							}
						}
					}
				}
				for (CtMethod method : mixinClass.getMethods()) {
					if (method.hasAnnotation(Rewrite.class)) {
						Rewrite annotation = (Rewrite) method.getAnnotation(Rewrite.class);
						CtMethod generatedMethod = new CtMethod(method.getReturnType(), mixinClass.getName().replace(".", "$") + "$"  + method.getName(), method.getParameterTypes(), target);
						generatedMethod.setBody(method, null);
						target.addMethod(generatedMethod);
						CtMethod targetMethod = null;
						for(CtMethod methodCandidate : target.getMethods()){
							//TODO check signature and use SRG
							if(methodCandidate.getName().equals(RebornLoadingCore.runtimeDeobfuscationEnabled && !annotation.targetSRG().isEmpty() ? annotation.targetSRG() : annotation.target())){
								targetMethod = methodCandidate;
								break;
							}
						}
						if(targetMethod == null){
							RebornLoadingCore.logHelper.error("Could not find method to inject into");
							throw new RuntimeException("Could not find method " + (RebornLoadingCore.runtimeDeobfuscationEnabled && !annotation.targetSRG().isEmpty() ? annotation.targetSRG() : annotation.target()) + " to inject into");
						}
						int pos = Modifier.isStatic(targetMethod.getModifiers()) ? 0 : 1;
						String[] methodArgs = new String[method.getParameterTypes().length];
						for (int i = 0; i < method.getParameterTypes().length; i++) {
							methodArgs[i] = "$" + (i + pos);
						}
						String src = "this." + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "(" + Joiner.on(",").join(methodArgs) + ");";
						if(annotation.location().equals("START")){
							targetMethod.insertBefore(src);
						} else if(annotation.location().equals("END")) {
							targetMethod.insertAfter(src);
						} else if(annotation.location().equals("RETURN")){
							targetMethod.setBody(src);
						} else {
							RebornLoadingCore.logHelper.error("Could not find valid injection location.");
							throw new RuntimeException("Could not find valid injection location.");
						}
					} else if (method.hasAnnotation(Inject.class)) {
						CtMethod generatedMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), target);
						generatedMethod.setBody(method, null);
						target.addMethod(generatedMethod);

					} else if (method.hasAnnotation(Constructor.class)){
						Constructor constructor = (Constructor) method.getAnnotation(Constructor.class);
						CtMethod generatedMethod = new CtMethod(method.getReturnType(), mixinClass.getName().replace(".", "$") + "$" + method.getName(), method.getParameterTypes(), target);
						generatedMethod.setBody(method, null);
						target.addMethod(generatedMethod);
						CtConstructor targetConstructor = target.getConstructor(constructor.description());
						String[] methodArgs = new String[method.getParameterTypes().length];
						for (int i = 0; i < method.getParameterTypes().length; i++) {
							methodArgs[i] = "$" + (i);
						}
						String src = "this." + mixinClass.getName().replace(".", "$") + "$" + method.getName() + "(" + Joiner.on(",").join(methodArgs) + ");";
						targetConstructor.insertAfter(src);
					}
				}
				for(CtField field : mixinClass.getFields()){
					if(field.hasAnnotation(Inject.class)){
						CtField generatedField = new CtField(field, target);
						System.out.println("Con:" + field.getConstantValue());
						target.addField(generatedField);
					}
				}
				for(CtConstructor constructor : mixinClass.getDeclaredConstructors()){
					System.out.println(constructor.getSignature());
				}
				for(CtClass iterface : mixinClass.getInterfaces()){
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
