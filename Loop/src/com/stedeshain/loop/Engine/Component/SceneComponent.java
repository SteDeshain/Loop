package com.stedeshain.loop.Engine.Component;

import com.stedeshain.loop.Engine.Scene.Scene;
import com.sun.istack.internal.NotNull;

public abstract class SceneComponent
{
	private Scene mMotherScene;
	
	private boolean mEnable = true;
	
	private String mName = null;
	private String mTag = "default";
	
	public SceneComponent() {}
	
	abstract public void update(float deltaTime);
	
	//TODO move to DrawableComponent ?
	/**
	 * synchronize drawable position with physics body
	 */
	public void updatePhysics() {};
	
	public void updateLastFrameValue() {}
	
	/**
	 * 
	 * @return : "no-name" if this component has no name, otherwise return its name
	 */
	public String getName()
	{
		return mName == null ? "no-name" : mName;
	}
	/**
	 * Invoker should keep every component having a different name from each other
	 * @param name
	 */
	public void setName(String name)
	{
		mName = name;
	}
	public boolean matchName(@NotNull String name)
	{
		if(mName == null)
			return false;
		
		return mName.equals(name);
	}

	public String getTag()
	{
		return mTag;
	}
	public void setTag(@NotNull String tag)
	{
		mTag = tag;
	}
	public boolean matchTag(@NotNull String tag)
	{
		return mTag.equals(tag);
	}

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
	 * Add this component to motherScene's mComponentsToRemoving list,
	 * it will be removed at the end of this frame.
	 * This method won't set motherScene(or motherLayer) to null, this task is handled by its motherScene.
	 * When overriding it, make sure to check whether motherScene == null, 
	 * if so, better just return.
	 */
	public void departFromScene()
	{
		if(mMotherScene == null)
			return;
		
		mMotherScene.appendRemovingComponent(this);
	}

}
