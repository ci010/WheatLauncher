package net.launcher.game.mod;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ci010
 */
public class RuntimeAnnotation
{
	private String modid, version, name, mcVersion, fingerprint, updateJson, dependencies;
	private boolean remoteVersion, saveVersion, clientOnly, severOnly;

	public RuntimeAnnotation(Map<String, Object> values)
	{
		for (Map.Entry<String, Object> entry : values.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	public String getModid()
	{
		return modid;
	}

	public String getMcVersion()
	{
		return mcVersion;
	}

	public String getFingerprint()
	{
		return fingerprint;
	}

	public String getUpdateJson()
	{
		return updateJson;
	}

	public String getDependencies()
	{
		return dependencies;
	}

	public boolean isRemoteVersion()
	{
		return remoteVersion;
	}

	public boolean isSaveVersion()
	{
		return saveVersion;
	}

	public boolean isClientOnly()
	{
		return clientOnly;
	}

	public boolean isSeverOnly()
	{
		return severOnly;
	}

	public void put(String s, Object o)
	{
		switch (s)
		{
			case "modid": modid = (String) o; break;
			case "version": version = (String) o; break;
			case "nameProperty": name = (String) o; break;
			case "certificateFingerprint": fingerprint = (String) o; break;
			case "updateJSON": updateJson = (String) o; break;
			case "dependencies": dependencies = (String) o; break;
			case "acceptableRemoteVersions": remoteVersion = (boolean) o; break;
			case "acceptableSaveVersions": saveVersion = (boolean) o; break;
			case "clientSideOnly": clientOnly = (boolean) o; break;
			case "severSideOnly": severOnly = (boolean) o; break;

			case "acceptedMinecraftVersions":
				mcVersion = (String) o;
				break;
		}
	}

	public String getModId()
	{
		return modid;
	}

	public String getVersion()
	{
		return version;
	}

	public String getName()
	{
		return name;
	}

	public static class Visitor extends ClassVisitor
	{
		private Set<Map<String, Object>> set;

		public Visitor(Set<Map<String, Object>> set)
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

	public static RuntimeAnnotation empty()
	{
		return new RuntimeAnnotation(Collections.emptyMap());
	}
}
