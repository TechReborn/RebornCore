package reborncore.jtraits;

import com.google.common.collect.Maps;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ASMUtils {

	private static Map<Integer, String> opcodes = new HashMap<Integer, String>();

	static {
		for (Field f : Opcodes.class.getFields()) {
			f.setAccessible(true);
			try {
				opcodes.put(f.getInt(null), f.getName());
			} catch (Exception e) {
			}
		}
	}

	public static String getOpcode(int opcode) {

		return opcodes.get(opcode);
	}

	public static int addInstructionsWithSuperRedirections(AbstractInsnNode node, List<AbstractInsnNode> added,
	                                                       int supercall, Mixin<?> mixin) {

		if (node instanceof FieldInsnNode) {
			FieldInsnNode f = (FieldInsnNode) node;
			if (matches(f.owner, mixin.getTraitType())) {
				if (f.name.equals("_super")) {
					added.add(new TypeInsnNode(Opcodes.CHECKCAST, mixin.getNewType()));
					return 1;
				} else {
					added.add(new FieldInsnNode(f.getOpcode(), mixin.getNewType(), f.name, f.desc));
					if (f.name.equals("_self"))
						return 3;
				}
			} else {
				added.add(new FieldInsnNode(f.getOpcode(), f.owner, f.name, f.desc));
			}
		} else if (node instanceof MethodInsnNode) {
			MethodInsnNode m = (MethodInsnNode) node;
			if (supercall == 1 && !(m.name.equals("<init>") || m.name.equals("<clinit>"))
				&& matches(m.owner, mixin.getParents())) {
				added.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
					trackClosestImplementation(m.name, m.desc, mixin.getParentClass()), m.name, m.desc, false));
				return 2;
			} else if (supercall == 3 && !(m.name.equals("<init>") || m.name.equals("<clinit>"))
				&& matches(m.owner, mixin.getParents())) {
				added.add(new MethodInsnNode(m.itf ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKESPECIAL, m.owner, m.name,
					m.desc, m.itf));
				return 2;
			}
			if (matches(m.owner, mixin.getParents())) {
				added.add(new MethodInsnNode(m.getOpcode(), mixin.getNewType(), m.name, m.desc, m.itf));
			} else {
				added.add(new MethodInsnNode(m.getOpcode(), m.owner, m.name, m.desc, m.itf));
			}
		} else if (node instanceof TypeInsnNode) {
			TypeInsnNode t = (TypeInsnNode) node;
			if (matches(t.desc, mixin.getParents())) {
				added.add(new TypeInsnNode(t.getOpcode(), mixin.getNewType()));
			} else {
				added.add(new TypeInsnNode(t.getOpcode(), t.desc));
			}
		} else if (node instanceof VarInsnNode) {
			VarInsnNode v = (VarInsnNode) node;
			if (v.var == 0) {
				added.add(new VarInsnNode(v.getOpcode(), v.var));
				added.add(new TypeInsnNode(Opcodes.CHECKCAST, mixin.getNewType()));
			}
		}
		return 0;
	}

	public static String nodeToString(AbstractInsnNode node) {

		String str = "[" + getOpcode(node.getOpcode()) + "] ";

		if (node instanceof FieldInsnNode) {
			FieldInsnNode n = (FieldInsnNode) node;
			str += "VARIABLE: owner=\"" + n.owner + "\" name=\"" + n.name + "\" desc=\"" + n.desc + "\"";
		} else if (node instanceof MethodInsnNode) {
			MethodInsnNode n = (MethodInsnNode) node;
			str += "METHOD: owner=\"" + n.owner + "\" name=\"" + n.name + "\" desc=\"" + n.desc + "\"";
		} else if (node instanceof TypeInsnNode) {
			TypeInsnNode n = (TypeInsnNode) node;
			str += "TYPE: desc=\"" + n.desc + "\"";
		} else if (node instanceof VarInsnNode) {
			VarInsnNode n = (VarInsnNode) node;
			str += "VAR: " + n.var;
		} else if (node instanceof LdcInsnNode) {
			LdcInsnNode n = (LdcInsnNode) node;
			str += "CONSTANT: \"" + n.cst + "\"";
		} else if (node instanceof LabelNode) {
			LabelNode n = (LabelNode) node;
			str += "LABEL: " + n.getLabel() + " - " + n.getLabel().hashCode();
		} else if (node instanceof JumpInsnNode) {
			JumpInsnNode n = (JumpInsnNode) node;
			str += "JUMP: " + n.label.getLabel() + " - " + n.label.getLabel().hashCode();
		} else if (node instanceof LineNumberNode) {
			LineNumberNode n = (LineNumberNode) node;
			str += "LINE: " + n.line;
		} else if (node instanceof FrameNode) {
			FrameNode n = (FrameNode) node;
			str += "FRAME: " + n.type;
		} else if (node instanceof InsnNode) {
			str = getOpcode(node.getOpcode());
		} else {
			str += node;
		}

		return str;
	}

	public static boolean matches(String str, String... others) {

		for (String s : others)
			if (str.equals(s))
				return true;
		return false;
	}

	public static ClassNode getClassNode(Class<?> clazz) {

		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(
				ClassLoadingHelper.instance.getResourceAsStream(clazz.getName().replace(".", "/") + ".class"));
			reader.accept(cnode, 0);

			return cnode;
		} catch (IOException ignore) {
			ignore.printStackTrace();
			return null;
		}
	}

	public static ClassNode getClassNode(byte[] bytecode) {

		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytecode);
		reader.accept(cnode, 0);
		return cnode;
	}

	public static int getReturnCode(String type) {

		if (type.equals("V"))
			return Opcodes.RETURN;
		if (type.equals("B") || type.equals("Z") || type.equals("S") || type.equals("I"))
			return Opcodes.IRETURN;
		else if (type.equals("L"))
			return Opcodes.LRETURN;
		else if (type.equals("F"))
			return Opcodes.FRETURN;
		else if (type.equals("D"))
			return Opcodes.DRETURN;
		return Opcodes.ARETURN;

	}

	public static String[] recursivelyFindClasses(Mixin<?> mixin) {

		Set<String> set = new HashSet<String>();
		recursivelyFindClasses(mixin, set);
		return set.toArray(new String[set.size()]);
	}

	private static void recursivelyFindClasses(Mixin<?> mixin, Set<String> set) {

		set.add(mixin.getParentType());
		set.add(mixin.getTraitType());
		recursivelyFindClasses(set);
		Mixin<?> next = ClassLoadingHelper.instance.findMixin(mixin.getParentType().replace("/", "."));
		if (next != null)
			recursivelyFindClasses(next, set);
	}

	private static void recursivelyFindClasses(Set<String> set) {

		int oldAmt = set.size();
		for (String s : new ArrayList<String>(set)) {
			try {
				Class<?> c = Class.forName(s.replace('/', '.'));
				Class<?> sc = c.getSuperclass();
				if (sc != null)
					set.add(sc.getName().replace('.', '/'));
				for (Class<?> i : c.getInterfaces())
					set.add(i.getName().replace('.', '/'));
			} catch (Exception ex) {
			}
		}
		if (oldAmt != set.size())
			recursivelyFindClasses(set);
	}

	public static MethodNode getMethod(String name, String desc, ClassNode clazz) {

		for (MethodNode m : clazz.methods)
			if (m.name.equals(name) && m.desc.equals(desc))
				return m;
		return null;
	}

	private static NodeCopier copier;

	public static void resetCopy(InsnList srcList) {

		copier = new NodeCopier(srcList);
	}

	public static void copyInsn(InsnList destList, AbstractInsnNode insn) {

		if (insn == null)
			return;

		copier.copyTo(insn, destList);
	}

	private static class NodeCopier {

		private Map<LabelNode, LabelNode> labelMap = Maps.newHashMap();

		public NodeCopier(InsnList sourceList) {

			for (AbstractInsnNode instruction = sourceList.getFirst(); instruction != null; instruction = instruction
				.getNext())
				if (instruction instanceof LabelNode)
					labelMap.put(((LabelNode) instruction), new LabelNode());
		}

		public void copyTo(AbstractInsnNode node, InsnList destination) {

			if (node == null)
				return;

			if (destination == null)
				return;

			destination.add(node.clone(labelMap));
		}
	}

	public static String trackClosestImplementation(String name, String desc, Class<?> clazz) {

		if (clazz == Object.class)
			return null;
		ClassNode n = getClassNode(clazz);
		for (MethodNode m : n.methods)
			if (m.name.equals(name) && m.desc.equals(desc))
				return Type.getInternalName(clazz);
		return trackClosestImplementation(name, desc, clazz.getSuperclass());
	}
}
