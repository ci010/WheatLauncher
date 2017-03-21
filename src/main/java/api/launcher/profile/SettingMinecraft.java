package api.launcher.profile;

/**
 * @author ci010
 */
public interface SettingMinecraft
{
	int getFov();

	void setFov(int fov);

	int getMaxFPS();

	void setMaxFPS(int maxFPS);

	int getRenderDistance();

	void setRenderDistance(int renderDistance);

	int getMipmapLevel();

	void setMipmapLevel(int mipmapLevel);

	int getParticles();

	void setParticles(int particles);

	int getAmbientOcclusion();

	void setAmbientOcclusion(int ambientOcclusion);

	boolean isAnaglygh3D();

	void setAnaglygh3D(boolean anaglygh3D);

	boolean isEnableVsync();

	void setEnableVsync(boolean enableVsync);

	boolean isFboEnable();

	void setFboEnable(boolean fboEnable);

	boolean isRenderClouds();

	void setRenderClouds(boolean renderClouds);

	boolean isUseVBO();

	void setUseVBO(boolean useVBO);

	boolean isGraphic();

	void setGraphic(boolean graphic);

	boolean isEntityShadow();

	void setEntityShadow(boolean entityShadow);

	boolean isForceUnicode();

	void setForceUnicode(boolean forceUnicode);

	boolean isLanguage();

	void setLanguage(boolean language);

	boolean isResourcePacks();

	void setResourcePacks(boolean resourcePacks);
}
