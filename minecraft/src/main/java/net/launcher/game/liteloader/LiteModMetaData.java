package net.launcher.game.liteloader;

import net.launcher.utils.StringUtils;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Objects;

/**
 * @author ci010
 */
public class LiteModMetaData
{
	public static Builder builder() {return new Builder();}

	public static Deserializer<LiteModMetaData, JSONObject> deserializer() {return deserializer0();}

	private String mcVersion, name, author, version, description, url;
	private int revision;
	private String tweakClass;
	private String[] dependsOn, requiredAPIs, classTransformerClasses;

	public String getMcVersion() {return mcVersion;}

	public String getName() {return name;}

	public String getAuthor() {return author;}

	public String getVersion() {return version;}

	public String getDescription() {return description;}

	public String getUrl() {return url;}

	public int getRevision() {return revision;}

	public String getTweakClass() {return tweakClass;}

	public String[] getDependsOn() {return dependsOn;}

	public String[] getRequiredAPIs() {return requiredAPIs;}

	public String[] getClassTransformerClasses() {return classTransformerClasses;}

	private LiteModMetaData(String mcVersion, String name, String author, String version, String description, String url, int revision, String tweakClass, String[] dependsOn, String[] requiredAPIs, String[] classTransformerClasses)
	{
		this.mcVersion = mcVersion;
		this.name = name;
		this.author = author;
		this.version = version;
		this.description = description;
		this.url = url;
		this.revision = revision;
		this.tweakClass = tweakClass;
		this.dependsOn = dependsOn;
		this.requiredAPIs = requiredAPIs;
		this.classTransformerClasses = classTransformerClasses;
	}

	private static Deserializer<LiteModMetaData, JSONObject> deserializer0()
	{
		return (serialized, context) -> builder().setMcVersion(serialized.getString("mcversion"))
				.setName(serialized.getString("name"))
				.setRevision(serialized.getInt("revision"))
				.setTweakClass(serialized.optString("tweakClass"))
				.setAuthor(serialized.optString("author"))
				.setVersion(serialized.optString("version"))
				.setUrl(serialized.optString("url"))
				.setDescription(serialized.optString("description"))
				.setClassTransformerClasses(serialized.optJSONArray("classTransformerClasses")
						.toList().stream().map(Object::toString).toArray(String[]::new))
				.setDependsOn(serialized.optJSONArray("dependsOn")
						.toList().stream().map(Object::toString).toArray(String[]::new))
				.setRequiredAPIs(serialized.optJSONArray("requiredAPIs")
						.toList().stream().map(Object::toString).toArray(String[]::new))
				.build();
	}

	public static class Builder implements org.to2mbn.jmccc.util.Builder<LiteModMetaData>
	{
		private String mcVersion = StringUtils.EMPTY, name = StringUtils.EMPTY, author = StringUtils.EMPTY, version = StringUtils.EMPTY,
				description = StringUtils.EMPTY, url = StringUtils.EMPTY;
		private int revision = 0;
		private String tweakClass = StringUtils.EMPTY;
		private String[] dependsOn = new String[0], requiredAPIs = dependsOn, classTransformerClasses = dependsOn;

		public Builder setMcVersion(String mcVersion)
		{
			Objects.requireNonNull(mcVersion);
			this.mcVersion = mcVersion;
			return this;
		}

		public Builder setName(String name)
		{
			Objects.requireNonNull(name);
			this.name = name;
			return this;
		}

		public Builder setAuthor(String author)
		{
			Objects.requireNonNull(author);
			this.author = author;
			return this;
		}

		public Builder setVersion(String version)
		{
			Objects.requireNonNull(version);
			this.version = version;
			return this;
		}

		public Builder setDescription(String description)
		{
			Objects.requireNonNull(description);
			this.description = description;
			return this;
		}

		public Builder setUrl(String url)
		{
			Objects.requireNonNull(url);
			this.url = url;
			return this;
		}

		public Builder setRevision(int revision)
		{
			Objects.requireNonNull(revision);
			this.revision = revision;
			return this;
		}

		public Builder setTweakClass(String tweakClass)
		{
			Objects.requireNonNull(tweakClass);
			this.tweakClass = tweakClass;
			return this;
		}

		public Builder setDependsOn(String[] dependsOn)
		{
			Objects.requireNonNull(dependsOn);
			this.dependsOn = dependsOn;
			return this;
		}

		public Builder setRequiredAPIs(String[] requiredAPIs)
		{
			Objects.requireNonNull(requiredAPIs);
			this.requiredAPIs = requiredAPIs;
			return this;
		}

		public Builder setClassTransformerClasses(String[] classTransformerClasses)
		{
			Objects.requireNonNull(classTransformerClasses);
			this.classTransformerClasses = classTransformerClasses;
			return this;
		}

		@Override
		public LiteModMetaData build()
		{
			return new LiteModMetaData(mcVersion, name, author, version, description, url, revision, tweakClass,
					dependsOn, requiredAPIs, classTransformerClasses);
		}
	}
}
