package com.stedeshain.loop.Engine.Scene;

import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Component.InputEvent;
import com.stedeshain.loop.Engine.Component.UIComponent;
import com.stedeshain.loop.Engine.Component.Body.AbstractBody;
import com.stedeshain.loop.Engine.Utils.Deepable;
import com.sun.istack.internal.NotNull;

public class Layer implements Deepable, Disposable
{
	private Scene mMotherScene;
	private Array<DrawableComponent> mDrawableComponents;
	/**
	 * to accelerate some calculation in rendering
	 */
	private Array<UIComponent> mUIComponents;
	
	private final boolean mIsUILayer;
	private long mDepth = 0;
	private String mName = null;
	private boolean mIsVisible = true;
	
	Layer(@NotNull Scene motherScene)
	{
		this(motherScene, false);
	}
	Layer(@NotNull Scene motherScene, boolean isUILayer)
	{
		mIsUILayer = isUILayer;
		mMotherScene = motherScene;
		mDrawableComponents = new Array<DrawableComponent>();
		mUIComponents = new Array<UIComponent>();
	}
	
	//package access
	//User cannot add component to a Layer directly,
	//but can only add to a Scene with a specified Layer index or name
	void addComponent(@NotNull DrawableComponent component)
	{
		if(mDrawableComponents.contains(component, true))
			return;
		
		mDrawableComponents.add(component);
		component.setMotherLayer(this);
		
		if(component instanceof UIComponent)
		{
			mUIComponents.add((UIComponent)component);
		}
	}
//	void addComponent(@NotNull UIComponent component)
//	{
//		if(mDrawableComponents.contains(component, true))
//			return;
//		
//		mDrawableComponents.add(component);
//		component.setMotherLayer(this);
//		mUIComponents.add((UIComponent)component);
//	}
	
	//package access
	void sortDrawables(Comparator<Deepable> comparator)
	{
		mDrawableComponents.sort(comparator);
	}

	//package access
	/**
	 * @param component to remove
	 * @return true if find the component and removed it successfully, false if can't find such component
	 */
	boolean removeComponent(@NotNull DrawableComponent component)
	{
		int index = -1;
		index = mDrawableComponents.indexOf(component, true);
		if(index >= 0)
		{
			mDrawableComponents.removeValue(component, true);
			if(component instanceof AbstractBody)
			{
				((AbstractBody)component).disposePhysics();
			}
			component.setMotherLayer(null);
			if(component instanceof UIComponent)
			{
				mUIComponents.removeValue((UIComponent)component, true);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	boolean containComponent(@NotNull DrawableComponent component)
	{
		return mDrawableComponents.contains(component, true);
	}

	//package access
	void draw(SpriteBatch batch)
	{		
		for(int i = mDrawableComponents.size - 1; i >= 0; i--)
		{
			if(mDrawableComponents.get(i).isVisible())
				mDrawableComponents.get(i).draw(batch);
		}
	}
	
	void resize(int width, int height)
	{
		if(!mIsUILayer)
			return;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			mUIComponents.get(i).resize(width, height);
		}
	}
	
	boolean fireInputEvent(InputEvent event)
	{
		if(!mIsUILayer)
			return false;
		
		boolean handled = false;
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			handled |= mUIComponents.get(i).fire(event);
			if(handled)
				break;
		}
		return handled;
	}
	
	UIComponent hit(float viewportX, float viewportY)
	{
		if(!mIsUILayer)
			return null;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			UIComponent curUI = mUIComponents.get(i);
			if(curUI.hit(viewportX, viewportY))
				return curUI;
		}
		return null;
	}
	
	void clearInputEvent()
	{
		if(!mIsUILayer)
			return;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			mUIComponents.get(i).clearInputEvent();
		}
	}

	public Scene getMotherScene()
	{
		return mMotherScene;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	public String getName()
	{
		return mName == null ? "no-name" : mName;
	}
	public boolean matchName(@NotNull String name)
	{
		if(mName == null)
			return false;
		
		return mName.equals(name);
	}
	
	public void setDepth(long depth)
	{
		mDepth = depth;
	}
	public long getDepth()
	{
		return mDepth;
	}
	
	public boolean isUILayer()
	{
		return mIsUILayer;
	}
	
	public boolean isVisible()
	{
		return mIsVisible;
	}
	public void setVisible(boolean visible)
	{
		mIsVisible = visible;
	}
	@Override
	public void dispose()
	{
		for(int i = mDrawableComponents.size - 1; i >= 0; i--)
		{
			mDrawableComponents.get(i).dispose();
		}
	}
}
