package com.stedeshain.loop.Engine.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.SceneComponent;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Component.UIComponent;

/**
 * No DrawableComponent can be added to this scene, but only SceneComponent or UIComponent
 * @author SteDeshain
 */
public class UIScene extends Scene
{

	public UIScene(Game motherGame)
	{
		super(motherGame, null);
	}
	
	@Override
	public void addComponent(SceneComponent component)
	{
		//filter DrawableComponent
		if(!(component instanceof UIComponent) && component instanceof DrawableComponent)
		{
			return;
		}
		
		//No DrawableComponent, but only SceneComponent or UIComponent was left
		super.addComponent(component);
	}
	/**
	 * User should not use this method in UIScene, cause it does nothing
	 */
	@Override
	public void addComponent(DrawableComponent component)
	{
		return;
	}
	@Override
	public void addComponent(UIComponent component)
	{
		super.addComponent(component);
	}

	/**
	 * No DrawableComponent camera for a UIScene
	 */
	@Override
	public OrthographicCamera getCamera()
	{
		return null;
	}
	
	@Override
	public void draw()
	{
		//only draw UI
		super.drawUI();
	}
}
