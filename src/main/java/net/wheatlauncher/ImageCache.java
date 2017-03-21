package net.wheatlauncher;

import api.launcher.MinecraftIcons;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author ci010
 */
public class ImageCache
{
	public static final CacheConfiguration<String, byte[]> CFG = CacheConfigurationBuilder
			.newCacheConfigurationBuilder(String.class, byte[].class,
					ResourcePoolsBuilder.newResourcePoolsBuilder().heap(32, MemoryUnit.MB))
			.withExpiry(Expirations.timeToLiveExpiration(new Duration(10, TimeUnit.MINUTES))).build();

	private static final byte[] NO_ICON = new byte[0];

	public static final String FORGE = "forge", CURSE_PROJECT = "curseforge.project",
			CURSE_CATEGORY = "curseforge.category";

	public static Cache<String, byte[]> getOrCreate(CacheManager manager, String alias)
	{
		Cache<String, byte[]> cache = manager.getCache(alias, String.class, byte[].class);
		if (cache == null) cache = manager.createCache(alias, CFG);
		return cache;
	}

	public static Image putCache(Cache<String, byte[]> cache, String key, Image image) throws IOException
	{
		Objects.requireNonNull(cache);
		Objects.requireNonNull(key);
		Objects.requireNonNull(image);
		if (image == MinecraftIcons.UNKNOWN) cache.put(key, NO_ICON);
		else
		{
			BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			ImageIO.write(bImage, "png", s);
			cache.put(key, s.toByteArray());
		}
		return image;
	}

	public static Image getCache(Cache<String, byte[]> cache, String key)
	{
		Objects.requireNonNull(cache);
		Objects.requireNonNull(key);
		byte[] bytes = cache.get(key);
		if (bytes == null) return null;
		if (bytes == NO_ICON) return MinecraftIcons.UNKNOWN;
		return new Image(new ByteArrayInputStream(bytes));
	}


}
