package com.stedeshain.loop.Engine.Component;

import com.stedeshain.loop.Engine.Scene.Scene;

public abstract class SceneComponent
{
	private Scene mMotherScene;
	
	private boolean mEnable = true;
	
	public SceneComponent() {}
	
	abstract public void update(float deltaTime);
	
	/**
	 * synchronize drawable position with physics body
	 */
	public void updatePhysics() {};
	
	public void updateLastFrameValue() {}
	
	public boolean isEnable()
	{
		return mEnable;
	}
	
	public void setEnable(boolean enable)
	{
		mEnable = enable;
	}
	public void enable()
	{
		setEnable(true);
	}
	public void disable()
	{
		setEnable(false);
	}
	
	/**
	 * called when added to a scene
	 */
	public void create() {}

	public Scene getMotherScene()
	{
		return mMotherScene;
	}
	public void setMotherScene(Scene motherScene)
	{
		mMotherScene = motherScene;
	}
	
	/**
	 * add this component to motherScene's mComponentsToRemoving,
	 * it will be removed at the end of this frame
	 */
	public void departFromScene()
	{
		if(mMotherScene == null)
			return;
		
		//TODO
	}

}
