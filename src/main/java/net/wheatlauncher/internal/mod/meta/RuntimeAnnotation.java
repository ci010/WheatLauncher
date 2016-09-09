package net.wheatlauncher.internal.mod.meta;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import net.wheatlauncher.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ci010
 */
public class RuntimeAnnotation implements Mod.Release
{
	private String modid, version, name, mcVersion, fingerprint, updateJson, dependencies;
	private boolean remoteVersion, saveVersion, clientOnly, severOnly;
	private Set<String> supportVersion;

	public RuntimeAnnotation(Map<String, Object> values)
	{
		for (Map.Entry<String, Object> entry : values.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	public void put(String s, Object o)
	{
		switch (s)
		{
			case "modid": modid = (String) o; break;
			case "version": version = (String) o; break;
			case "name": name = (String) o; break;
			case FINGURPRINT: fingerprint = (String) o; break;
			case UPDATE_JSON: updateJson = (String) o; break;
			case DEPENDENCIES: dependencies = (String) o; break;
			case ACCEPTABLE_REMOTE_VERSION: remoteVersion = (boolean) o; break;
			case ACCEPTABLE_SAVE_VERSION: saveVersion = (boolean) o; break;
			case CLIENT_ONLY: clientOnly = (boolean) o; break;
			case SEVER_ONLY: severOnly = (boolean) o; break;

			case "acceptedMinecraftVersions":
				mcVersion = (String) o;
				break;
		}
	}

	@Override
	public String getModId()
	{
		return modid;
	}

	public String getVersion()
	{
		return version;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Set<String> getAllSupportMinecraftVersions()
	{
		return supportVersion;
	}

	@Override
	public Object getMeta(String s)
	{
		switch (s)
		{
			case FINGURPRINT: return fingerprint;
			case UPDATE_JSON: return updateJson;
			case DEPENDENCIES: return dependencies;
			case ACCEPTABLE_REMOTE_VERSION: return remoteVersion;
			case ACCEPTABLE_SAVE_VERSION: return saveVersion;
			case CLIENT_ONLY: return clientOnly;
			case SEVER_ONLY: return severOnly;
		}
		return null;
	}

//	@Override
	public Mod.Release merge(Mod.Release meta)
	{
		if (meta == this)
			return this;
		if (COMPARATOR.compare(this, meta) == 0)
		{
			if (meta instanceof RuntimeAnnotation) //this should not happen... it's better to just ignore it than throw exception
				return this;
			if (meta instanceof ModInfo)
				return new ReleaseImpl((ModInfo) meta, this);
		}
		return null;
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
			if (s.equals("Lnet/minecraftforge/fml/common/ModFile;"))
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
}
