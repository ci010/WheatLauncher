package net.launcher.impl;

import api.launcher.profile.SettingMinecraft;

/**
 * @author ci010
 */
public class SettingMinecraftImple implements SettingMinecraft
{
	private int fov, maxFPS, renderDistance, mipmapLevel, particles, ambientOcclusion;
	private boolean anaglygh3D, enableVsync, fboEnable, renderClouds, useVBO, graphic, entityShadow, forceUnicode,
			language, resourcePacks;

	@Override
	public int getFov() {return fov;}

	@Override
	public void setFov(int fov)
	{
		this.fov = fov;
	}

	@Override
	public int getMaxFPS() {return maxFPS;}

	@Override
	public void setMaxFPS(int maxFPS)
	{
		this.maxFPS = maxFPS;
	}

	@Override
	public int getRenderDistance() {return renderDistance;}

	@Override
	public void setRenderDistance(int renderDistance)
	{
		this.renderDistance = renderDistance;
	}

	@Override
	public int getMipmapLevel() {return mipmapLevel;}

	@Override
	public void setMipmapLevel(int mipmapLevel)
	{
		this.mipmapLevel = mipmapLevel;
	}

	@Override
	public int getParticles() {return particles;}

	@Override
	public void setParticles(int particles)
	{
		this.particles = particles;
	}

	@Override
	public int getAmbientOcclusion() {return ambientOcclusion;}

	@Override
	public void setAmbientOcclusion(int ambientOcclusion)
	{
		this.ambientOcclusion = ambientOcclusion;
	}

	@Override
	public boolean isAnaglygh3D() {return anaglygh3D;}

	@Override
	public void setAnaglygh3D(boolean anaglygh3D)
	{
		this.anaglygh3D = anaglygh3D;
	}

	@Override
	public boolean isEnableVsync() {return enableVsync;}

	@Override
	public void setEnableVsync(boolean enableVsync)
	{
		this.enableVsync = enableVsync;
	}

	@Override
	public boolean isFboEnable() {return fboEnable;}

	@Override
	public void setFboEnable(boolean fboEnable)
	{
		this.fboEnable = fboEnable;
	}

	@Override
	public boolean isRenderClouds() {return renderClouds;}

	@Override
	public void setRenderClouds(boolean renderClouds)
	{
		this.renderClouds = renderClouds;
	}

	@Override
	public boolean isUseVBO() {return useVBO;}

	@Override
	public void setUseVBO(boolean useVBO)
	{
		this.useVBO = useVBO;
	}

	@Override
	public boolean isGraphic() {return graphic;}

	@Override
	public void setGraphic(boolean graphic)
	{
		this.graphic = graphic;
	}

	@Override
	public boolean isEntityShadow() {return entityShadow;}

	@Override
	public void setEntityShadow(boolean entityShadow)
	{
		this.entityShadow = entityShadow;
	}

	@Override
	public boolean isForceUnicode() {return forceUnicode;}

	@Override
	public void setForceUnicode(boolean forceUnicode)
	{
		this.forceUnicode = forceUnicode;
	}

	@Override
	public boolean isLanguage() {return language;}

	@Override
	public void setLanguage(boolean language)
	{
		this.language = language;
	}

	@Override
	public boolean isResourcePacks() {return resourcePacks;}

	@Override
	public void setResourcePacks(boolean resourcePacks)
	{
		this.resourcePacks = resourcePacks;
	}
}
