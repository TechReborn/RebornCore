package reborncore.mixin.implementations.forge;

import javassist.*;
import javassist.bytecode.ConstPool;
import reborncore.mixin.api.Remap;
import reborncore.mixin.api.Rewrite;

import reborncore.mixin.transformer.util.ClassRenamer;
import reborncore.mixin.transformer.util.ConstPoolEditor;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.mixin.transformer.IMixinRemap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Mark on 01/01/2017.
 */
public class ForgeRemapper implements IMixinRemap {
	@Override
	public void remap(CtClass mixinClass, ClassPool classPool) {
		if (!MixinForgeLoadingCore.runtimeDeobfuscationEnabled) {
			return;
		}
		List<FieldData> fieldDataList = new ArrayList<>();
		for (CtField field : mixinClass.getFields()) {
			if (field.hasAnnotation(Remap.class)) {
				Remap remap = null;
				try {
					remap = (Remap) field.getAnnotation(Remap.class);
				} catch (ClassNotFoundException e) {
					//Should not happen, as we checked for it
					e.printStackTrace();
					continue;
				}
				fieldDataList.add(new FieldData(field, field.getName(), remap.SRG()));
			}
		}
		if (MixinForgeLoadingCore.runtimeDeobfuscationEnabled && !fieldDataList.isEmpty()) {
			ConstPool constPool = mixinClass.getClassFile().getConstPool();
			ConstPoolEditor editor = new ConstPoolEditor(constPool);
			for (int i = 1; i < constPool.getSize(); i++) {
				switch (constPool.getTag(i)) {
					case ConstPool.CONST_Fieldref: {
						for (FieldData fieldData : fieldDataList) {
							if (constPool.getFieldrefName(i).equals(fieldData.name)) {
								editor.changeMemberrefNameAndType(i, fieldData.srg, fieldData.field.getSignature());
							}
						}
					}
				}
			}
		}
		ClassRenamer.renameClasses(mixinClass, className -> {
			if (!MixinForgeLoadingCore.runtimeDeobfuscationEnabled || !className.startsWith("net/minecraft")) {
				return null;
			}
			return FMLDeobfuscatingRemapper.INSTANCE.unmap(className);
		});
		if (mixinClass.getClassFile().getSuperclass().startsWith("net.minecraft")) {
			if (MixinForgeLoadingCore.runtimeDeobfuscationEnabled) {
				try {
					mixinClass.setSuperclass(classPool.get(FMLDeobfuscatingRemapper.INSTANCE.unmap(mixinClass.getClassFile().getSuperclass().replace(".", "/"))));
				} catch (CannotCompileException | NotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public Optional<Pair<String, String>> getFullTargetName(Rewrite annotation, String name) {
		if (MixinForgeLoadingCore.runtimeDeobfuscationEnabled) {
			String targetName = !annotation.targetSRG().isEmpty() ? annotation.targetSRG() : annotation.target();
			Map<String, String> methodMap = getMethodMap(name);
			for (Map.Entry<String, String> entry : methodMap.entrySet()) {
				if (entry.getValue().equals(targetName)) {
					targetName = entry.getKey().split("\\(")[0];
					return Optional.of(new ImmutablePair<>(targetName, entry.getKey()));
				}
			}
		}
		return Optional.empty();
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
