package reborncore.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by modmuss50 on 16/11/16.
 */
public class ClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.equals("net.minecraft.item.ItemStack")) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			MethodVisitor mv;

			int baseLine = 1700;
			{
				mv = writer.visitMethod(ACC_PUBLIC, "<init>", "(Lnet/minecraft/nbt/NBTTagCompound;)V", null, null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(baseLine + 11, l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLineNumber(baseLine + 12, l1);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLineNumber(baseLine + 13, l2);
				mv.visitInsn(RETURN);
				Label l3 = new Label();
				mv.visitLabel(l3);
				mv.visitLocalVariable("this", "Lnet/minecraft/item/ItemStack;", null, l0, l3, 0);
				mv.visitLocalVariable("tagCompound", "Lnet/minecraft/nbt/NBTTagCompound;", null, l0, l3, 1);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}
			classNode.accept(writer);
			return writer.toByteArray();
		}
		return basicClass;
	}

}
