package net.launcher.game.forge;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ci010
 */
class ModAnnotationVisitor extends ClassVisitor
{
	private Set<Map<String, Object>> set;

	ModAnnotationVisitor(Set<Map<String, Object>> set)
	{
		super(Opcodes.ASM5);
		this.set = set;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String s, boolean b)
	{
		if (s.equals("Lnet/minecraftforge/fml/common/Mod;"))
			return new AnnoVisitor(new HashMap<>());
		return null;
	}

	private class AnnoVisitor extends AnnotationVisitor
	{
		private Map<String, Object> capture;

		public AnnoVisitor(Map<String, Object> capture)
		{
			super(Opcodes.ASM5);
			this.capture = capture;
		}

		@Override
		public void visit(String s, Object o)
		{
			capture.put(s, o);
		}

		@Override
		public void visitEnd()
		{
			set.add(capture);
		}
	}
}
