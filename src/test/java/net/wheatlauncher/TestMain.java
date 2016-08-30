package net.wheatlauncher;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.Session;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.yggdrasil.YggdrasilProfileServiceBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ci010
 */
public class TestMain
{
	public static void main(String[] args) throws Exception
	{
//		testInvalidPassworld();
		testASM();
//		testDumpClass();
	}

	public static Pattern classFile = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");

	static void testDumpClass()
	{
		ClassLoader classLoader = TestMain.class.getClassLoader();
		System.out.println(classLoader instanceof URLClassLoader);
		URLClassLoader loader = (URLClassLoader) classLoader;
		for (URL url : loader.getURLs())
		{
			System.out.println(url);
		}
	}

	//ASM won't load class!
	static void testASM() throws IOException, ClassNotFoundException
	{

		JarFile jarFile = new JarFile(new File("C:\\Users\\CIJhn\\Workspace\\WheatLauncher\\src\\main\\resources" +
				"\\BiblioCraft[v2.0.1][MC1.8.9].jar"));
		for (JarEntry jarEntry : Collections.list(jarFile.entries()))
		{
			Matcher match = classFile.matcher(jarEntry.getName());
			if (match.matches())
			{
				new ClassReader(jarFile.getInputStream(jarEntry)).accept(new Visitor(), 0);
			}
		}

	}

	static class AnnoVisitor extends AnnotationVisitor
	{
		public AnnoVisitor()
		{
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(String s, Object o)
		{
			System.out.println("visit " + s + " " + o);
		}

		@Override
		public void visitEnum(String s, String s1, String s2)
		{
			System.out.println("visit enum " + s + " " + s1 + " " + s2);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String s, String s1)
		{
			return new AnnoVisitor();
		}

		@Override
		public AnnotationVisitor visitArray(String s)
		{
			return new AnnoVisitor();
		}
	}

	static class Visitor extends ClassVisitor
	{
		public Visitor()
		{
			super(Opcodes.ASM5);
		}
		@Override
		public AnnotationVisitor visitAnnotation(String s, boolean b)
		{
			if (s.equals("Lnet/minecraftforge/fml/common/Mod;"))
				return new AnnoVisitor();
			return null;
		}
	}

	static void testProfile() throws AuthenticationException
	{
		YggdrasilAuthenticator password = YggdrasilAuthenticator.password("18211182378@163.com", "123456789x");
		Session session = password.session();
		ProfileService profileService = YggdrasilProfileServiceBuilder.buildDefault();
		for (GameProfile gameProfile : session.getProfiles())
		{
			Map<TextureType, Texture> textures = profileService.getTextures(gameProfile);
			textures.forEach((textureType, texture) -> {
				System.out.println(textureType + ": " + texture);
			});
			System.out.println(gameProfile);
		}
	}

	//org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException: ForbiddenOperationException: Invalid credentials. Invalid username or password.
	static void testInvalidPassworld()
	{
		try
		{
			YggdrasilAuthenticator password = YggdrasilAuthenticator.password("a", "12345678");
		}
		catch (AuthenticationException e)
		{

			e.printStackTrace();
		}
	}

	//org.to2mbn.jmccc.auth.AuthenticationException: java.net.UnknownHostException: authserver.mojang.com
	static void testInvalidNetwork()
	{
		try
		{
			YggdrasilAuthenticator password = YggdrasilAuthenticator.password("a", "12345678");
		}
		catch (AuthenticationException e)
		{

			e.printStackTrace();
		}

	}
}
