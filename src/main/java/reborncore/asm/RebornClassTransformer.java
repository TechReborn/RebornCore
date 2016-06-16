package reborncore.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import reborncore.shields.ShieldHooks;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornClassTransformer implements IClassTransformer
{
	String className = "net.minecraft.item.Item";
	String obofClassName = "ado";
	String descriptor = "(ILnet/minecraft/util/ResourceLocation;Lnet/minecraft/item/Item;)V";
	String obofDescriptor = "(ILkl;Lado;)V";
	String methordName = "registerItem";
	String obofMethordName = "a";

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		// This replaces the item registry to inject new items
		if (name.equals(className) || name.equals("ado"))
		{

			FMLLog.log("RebornCore", Level.INFO, String.valueOf("Found item class"));
			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode method = findMethodNodeOfClass(classNode, RebornASM.deobfuscatedEnvironment ? methordName : obofMethordName, RebornASM.deobfuscatedEnvironment
					? descriptor : obofDescriptor);

			InsnList toInject = new InsnList();
			toInject.add(new VarInsnNode(Opcodes.ILOAD, 0));
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
			toInject.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC, Type.getInternalName(ShieldHooks.class), methordName, RebornASM.deobfuscatedEnvironment
							? descriptor : obofDescriptor,
					false));
			toInject.add(new InsnNode(Opcodes.RETURN));

			method.instructions.insertBefore(findFirstInstruction(method), toInject);

			return writeClassToBytes(classNode);
		}
		return bytes;
	}

	private ClassNode readClassFromBytes(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				return method;
			}
		}
		return null;
	}

	public AbstractInsnNode findFirstInstruction(MethodNode method)
	{
		return getOrFindInstruction(method.instructions.getFirst());
	}

	public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck)
	{
		return getOrFindInstruction(firstInsnToCheck, false);
	}

	public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection
				? instruction.getPrevious() : instruction.getNext())
		{
			if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
				return instruction;
		}
		return null;
	}

	private byte[] writeClassToBytes(ClassNode classNode)
	{
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
