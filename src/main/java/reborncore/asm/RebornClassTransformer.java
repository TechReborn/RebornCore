package reborncore.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import reborncore.RebornCore;
import reborncore.shields.ShieldHooks;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        //This is used to render custom shields
        if(name.equals("net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer")){
            RebornCore.logHelper.info("Patching TileEntityItemStackRenderer");
            boolean isObfuscated = !name.equals(transformedName);
            ClassNode classNode = readClassFromBytes(bytes);
            MethodNode method = findMethodNodeOfClass(classNode, isObfuscated ? "a" : "renderByItem", isObfuscated ? "(Ladq;)V" : "(Lnet/minecraft/item/ItemStack;)V");

            InsnList toInject = new InsnList();
            toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
            toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
            toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ShieldHooks.class), "renderByItem", isObfuscated ? "(Ladq;)V" : "(Lnet/minecraft/item/ItemStack;)V", false));
            toInject.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insertBefore(findFirstInstruction(method), toInject);

            return writeClassToBytes(classNode);
        }
        return bytes;
    }

    private ClassNode readClassFromBytes(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        return classNode;
    }

    private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
                return method;
            }
        }
        return null;
    }

    public AbstractInsnNode findFirstInstruction(MethodNode method) {
        return getOrFindInstruction(method.instructions.getFirst());
    }

    public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    public AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
                return instruction;
        }
        return null;
    }

    private byte[] writeClassToBytes(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
