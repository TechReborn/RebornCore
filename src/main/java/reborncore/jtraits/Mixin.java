package reborncore.jtraits;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Mixin<T>
{

	public static final String getName(Class<?> clazz, Class<?> trait)
	{

		return Type.getInternalName(clazz) + "$$" + trait.getSimpleName();
	}

	private String parentType;
	private ClassNode parentNode;
	private Class<T> parentClass;

	private String traitType;
	private ClassNode traitNode;
	private Class<?> traitClass;

	private String newType;
	private String castType;
	private Class<T> result;

	private String[] parents;

	private boolean annCheckMixin;
	private String annCheckMixinField;
	private String annCheckMixinOwner;

	public Mixin(Class<T> clazz, Class<?> trait)
	{

		parentType = Type.getInternalName(clazz);
		parentClass = clazz;
		traitType = Type.getInternalName(trait);
		traitClass = trait;

		updateNodes();

		newType = getName(clazz, trait);
		castType = newType;

		parents = ASMUtils.recursivelyFindClasses(this);

		Class<?> c = clazz;
		do
		{
			Annotation.CheckMixin a = c.getAnnotation(Annotation.CheckMixin.class);
			if (a == null)
				continue;

			annCheckMixin = true;
			annCheckMixinField = a.value();
			annCheckMixinOwner = c.getName().replace('.', '/');
			break;
		} while ((c = c.getSuperclass()) != null && c != Object.class);
	}

	public void updateNodes()
	{

		parentNode = ASMUtils.getClassNode(parentClass);
		traitNode = ASMUtils.getClassNode(traitClass);
	}

	public void updateNodes(byte[] traitBytes)
	{

		parentNode = ASMUtils.getClassNode(parentClass);
		traitNode = ASMUtils.getClassNode(traitClass);
	}

	public byte[] mixin_do()
	{

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		writer.visit(V1_6, ACC_PUBLIC, newType, null, parentType,
				traitNode.interfaces.toArray(new String[traitNode.interfaces.size()]));
		writer.visitSource(traitType.substring(traitType.lastIndexOf("/") + 1) + ".java", null);

		transferParentFields(writer);
		transferTraitFields(writer);

		bridgeMethods(writer);

		boolean hasSelfObject = false;
		for (FieldNode f : parentNode.fields)
		{
			if (f.name.equals("_self"))
			{
				hasSelfObject = true;
				break;
			}
		}
		if (!hasSelfObject)
			writer.visitField(ACC_PUBLIC | ACC_SYNTHETIC, "_self", "Ljava/lang/Object;", null, null);

		return writer.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public Class<T> mixin()
	{

		if (result != null)
			return result;

		return result = (Class<T>) ClassLoadingHelper.instance.addMixin(newType.replace('/', '.'), mixin_do(), this);
	}

	private void transferParentFields(ClassWriter writer)
	{

		for (FieldNode f : parentNode.fields)
		{
			if (f.name.equals("_super"))
				continue;
			FieldVisitor v = writer.visitField(ACC_PUBLIC, f.name, f.desc, null, f.value);
			if (f.visibleAnnotations != null)
			{
				for (AnnotationNode a : f.visibleAnnotations)
				{
					if (a.values == null)
						continue;
					AnnotationVisitor av = v.visitAnnotation(a.desc, true);
					Iterator<Object> it = a.values.iterator();
					while (it.hasNext())
					{
						String key = (String) it.next();
						Object val = it.next();
						try
						{
							if (val instanceof Object[] && !(val instanceof byte[] || val instanceof boolean[]
									|| val instanceof short[] || val instanceof char[] || val instanceof int[]
									|| val instanceof long[] || val instanceof float[] || val instanceof double[]))
							{
								av = av.visitArray(key);
								int i = 0;
								for (Object o : (Object[]) val)
								{
									av.visit("" + i, o);
									i++;
								}
							} else
							{
								av.visit(key, val);
							}
						} catch (Exception ex)
						{
							if (MixinFactory.debug)
								new RuntimeException("Invalid key/value: " + key + " - " + val, ex).printStackTrace();
						}
					}
				}
			}
			v.visitEnd();
		}
	}

	private void transferTraitFields(ClassWriter writer)
	{

		for (FieldNode f : traitNode.fields)
		{
			if (f.name.equals("_super"))
				continue;
			FieldVisitor v = writer.visitField(ACC_PUBLIC, f.name, f.desc, null, f.value);
			if (f.visibleAnnotations != null)
			{
				for (AnnotationNode a : f.visibleAnnotations)
				{
					AnnotationVisitor av = v.visitAnnotation(a.desc, true);
					Iterator<Object> it = a.values.iterator();
					while (it.hasNext())
						av.visit((String) it.next(), it.next());
				}
			}
			v.visitEnd();
		}
	}

	private void bridgeMethods(ClassWriter writer)
	{

		List<String> constructors = new ArrayList<String>();
		for (MethodNode m : traitNode.methods)
		{
			MethodVisitor v = writer.visitMethod(
					ACC_PUBLIC | ACC_SYNTHETIC
							| (m.access & ~ACC_ABSTRACT & ~ACC_INTERFACE & ~ACC_PROTECTED & ~ACC_PRIVATE),
					m.name, m.desc, null, null);
			v.visitCode();

			ASMUtils.resetCopy(m.instructions);
			v.visitLabel(new Label());

			// Transfer parent node
			if (m.name.equals("<init>") || m.name.equals("<clinit>"))
			{
				if (m.name.equals("<init>"))
					constructors.add(m.name + m.desc);

				v.visitVarInsn(ALOAD, 0);
				int index = 1;
				for (Type t : Type.getArgumentTypes(m.desc))
				{
					v.visitVarInsn(
							t == Type.BOOLEAN_TYPE || t == Type.BYTE_TYPE || t == Type.CHAR_TYPE || t == Type.INT_TYPE
									|| t == Type.LONG_TYPE || t == Type.SHORT_TYPE ? ILOAD
											: (t == Type.DOUBLE_TYPE ? DLOAD : (t == Type.FLOAT_TYPE ? FLOAD : ALOAD)),
							index);
					index += t.getSize();
				}
				v.visitMethodInsn(INVOKESPECIAL, parentType, m.name, m.desc, false);

				v.visitVarInsn(ALOAD, 0);
				v.visitVarInsn(ALOAD, 0);
				v.visitFieldInsn(PUTFIELD, newType, "_self", "Ljava/lang/Object;");

				if (annCheckMixin)
				{
					v.visitVarInsn(ALOAD, 0);
					v.visitLdcInsn(1);
					v.visitFieldInsn(PUTFIELD, annCheckMixinOwner, annCheckMixinField, "Z");
				}
			}

			// Get matching method and transfer it if needed
			InsnList list = new InsnList();
			ListIterator<AbstractInsnNode> originalInsns = m.instructions.iterator();
			List<AbstractInsnNode> added = new ArrayList<AbstractInsnNode>();
			int supercall = 0;
			while (originalInsns.hasNext())
			{
				AbstractInsnNode node = originalInsns.next();
				AbstractInsnNode next = node.getNext(), prev = node.getPrevious();
				if (next != null && node instanceof VarInsnNode && ((VarInsnNode) node).var == 0
						&& next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("<init>"))
					continue;
				if (prev != null && prev instanceof VarInsnNode && ((VarInsnNode) prev).var == 0
						&& node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals("<init>"))
					continue;

				int result = ASMUtils.addInstructionsWithSuperRedirections(node, added, supercall, this);
				if (result == 1)
					supercall = 1;
				else if (result == 2)
					supercall = 2;
				else if (result == 3)
					supercall = 3;

				if (added.isEmpty())
				{
					ASMUtils.copyInsn(list, node);
				} else
				{
					for (AbstractInsnNode n_ : added)
						list.add(n_);
					added.clear();
				}
			}
			list.accept(v);

			v.visitInsn(ASMUtils.getReturnCode(m.desc.substring(m.desc.lastIndexOf(")") + 1)));

			v.visitMaxs(m.maxStack + 1, m.maxLocals + 1);

			if (m.visibleAnnotations != null)
			{
				for (AnnotationNode a : m.visibleAnnotations)
				{
					AnnotationVisitor av = v.visitAnnotation(a.desc, true);
					Iterator<Object> it = a.values.iterator();
					while (it.hasNext())
						av.visit((String) it.next(), it.next());
				}
			}
			v.visitEnd();
		}
		for (MethodNode m : parentNode.methods)
		{
			if (m.name.equals("<init>") && !constructors.contains(m.name + m.desc))
			{
				MethodVisitor v = writer.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, m.name, m.desc, null, null);
				v.visitCode();

				v.visitVarInsn(ALOAD, 0);
				int index = 1;
				for (Type t : Type.getArgumentTypes(m.desc))
				{
					v.visitVarInsn(t == Type.BOOLEAN_TYPE || t == Type.BYTE_TYPE || t == Type.CHAR_TYPE
							|| t == Type.INT_TYPE || t == Type.SHORT_TYPE
									? ILOAD
									: (t == Type.LONG_TYPE ? LLOAD
											: (t == Type.DOUBLE_TYPE ? DLOAD : (t == Type.FLOAT_TYPE ? FLOAD : ALOAD))),
							index);
					index += t.getSize();
				}
				v.visitMethodInsn(INVOKESPECIAL, parentType, m.name, m.desc, false);

				v.visitVarInsn(ALOAD, 0);
				v.visitVarInsn(ALOAD, 0);
				v.visitFieldInsn(PUTFIELD, newType, "_self", "Ljava/lang/Object;");

				if (annCheckMixin)
				{
					v.visitVarInsn(ALOAD, 0);
					v.visitLdcInsn(1);
					v.visitFieldInsn(PUTFIELD, annCheckMixinOwner, annCheckMixinField, "Z");
				}

				v.visitInsn(RETURN);

				v.visitMaxs(m.maxStack + 1, m.maxLocals + 1);
				v.visitEnd();
			}
		}
	}

	public String getParentType()
	{

		return parentType;
	}

	public ClassNode getParentNode()
	{

		return parentNode;
	}

	public Class<T> getParentClass()
	{

		return parentClass;
	}

	public String getTraitType()
	{

		return traitType;
	}

	public ClassNode getTraitNode()
	{

		return traitNode;
	}

	public String getNewType()
	{

		return newType;
	}

	public String getCastType()
	{

		return castType;
	}

	public String[] getParents()
	{

		return parents;
	}
}
